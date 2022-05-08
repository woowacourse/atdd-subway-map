package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Transactional
@Service
public class LineService {
    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        checkExistLineByName(lineRequest);
        final Long id = lineDao.save(lineRequest);
        final Section section = saveSection(id, lineRequest.getUpStationId(), lineRequest.getDownStationId(),
                lineRequest.getDistance());
        final Line line = new Line(id, lineRequest.getName(), lineRequest.getColor(), section);

        final Station upStation = findByStationId(section.getUpStationId());
        final Station downStation = findByStationId(section.getDownStationId());
        final StationResponse upStationResponse = new StationResponse(upStation.getId(), upStation.getName());
        final StationResponse downStationResponse = new StationResponse(downStation.getId(), downStation.getName());

        return new LineResponse(id, line.getName(), line.getColor(), List.of(upStationResponse, downStationResponse));
    }

    private void checkExistLineByName(wooteco.subway.dto.LineRequest lineRequest) {
        if (lineDao.hasLine(lineRequest.getName())) {
            throw new IllegalArgumentException("같은 이름의 노선이 존재합니다.");
        }
    }

    private Section saveSection(Long newLineId, Long upStationId, Long downStationId, int distance) {
        final Section section = new Section(newLineId, upStationId, downStationId, distance);
        Long sectionId = sectionDao.save(section);
        return new Section(sectionId, newLineId, upStationId, downStationId, distance);
    }

    private Station findByStationId(Long id) {
        return stationDao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("구간 상행역 생성에 오류가 발생했습니다."));
    }

    public Long addSection(Long lineId, SectionRequest sectionRequest) {
        final Line line = loadLine(lineId);
        validateLine(sectionDao.findByLineId(lineId));

        final Section section = new Section(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
                sectionRequest.getDistance());
        line.addSection(section);
        final Section existSection = sectionDao.findBySameUpOrDownStationId(lineId, section)
                .orElseThrow(() -> new IllegalArgumentException("구간이 존재하지 않습니다."));
        saveSplitSection(existSection, section);
        sectionDao.save(section);
        return section.getId();
    }

    private Line loadLine(Long lineId) {
        final Line findLine = lineDao.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("해당 노선이 존재하지 않습니다."));
        final List<Section> sections = sectionDao.findByLineId(lineId);
        return new Line(findLine.getId(), findLine.getName(), findLine.getColor(), sections);
    }

    private void saveSplitSection(Section existSection, Section section) {
        final int newDistance = existSection.getDistance() - section.getDistance();
        if (existSection.hasSameUpStation(section)) {
            sectionDao.updateUpStation(existSection.getId(), section.getDownStationId(), newDistance);
            return;
        }
        sectionDao.updateDownStation(existSection.getId(), section.getUpStationId(), newDistance);
    }

    private void validateLine(List<Section> sections) {
        if (sections.isEmpty()) {
            throw new NoSuchElementException("구간이 존재하지 않는 노선입니다. 오류가 발생했습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        final List<Line> lines = lineDao.findAll();
        for (Line line : lines) {
            makeLineResponseByLine(line);
        }
        return lines.stream()
                .map(this::makeLineResponseByLine)
                .collect(Collectors.toUnmodifiableList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long id) {
        final Line line = checkExistLineById(id);
        return makeLineResponseByLine(line);
    }

    private LineResponse makeLineResponseByLine(Line line) {
        final List<Section> sections = sectionDao.findByLineId(line.getId());
        final List<Station> stations = findStationsBySections(sections);

        final List<StationResponse> stationResponses = makeStationResponseByStation(stations);
        Comparator<StationResponse> comparator = (o1, o2) -> Long.valueOf(o1.getId() - o2.getId()).intValue();
        stationResponses.sort(comparator);

        return new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses);
    }

    private List<Station> findStationsBySections(List<Section> sections) {
        final List<Station> stations = new ArrayList<>();
        for (Section section : sections) {
            Station upStation = findByStationId(section.getUpStationId());
            Station downStation = findByStationId(section.getDownStationId());
            stations.add(upStation);
            stations.add(downStation);
        }
        return stations.stream()
                .distinct()
                .collect(Collectors.toUnmodifiableList());
    }

    private List<StationResponse> makeStationResponseByStation(List<Station> stations) {
        return stations.stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
    }

    public void updateLine(Long id, String name, String color) {
        checkExistLineById(id);
        lineDao.updateById(id, name, color);
    }

    public void deleteLine(Long id) {
        checkExistLineById(id);
        lineDao.deleteById(id);
        final List<Section> sections = sectionDao.findByLineId(id);
        sectionDao.delete(sections);
    }

    private Line checkExistLineById(Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 노선이 존재하지 않습니다."));
    }

    public void deleteSection(Long lineId, Long stationId) {
//        final Station station = stationDao.findById(stationId)
//                .orElseThrow(() -> new IllegalArgumentException(stationId + "번에 해당하는 지하철역이 존재하지 않습니다."));
//        final Line line = lineDao.findById(lineId)
//                .orElseThrow(() -> new IllegalArgumentException(lineId + "번에 해당하는 노선이 존재하지 않습니다."));
//        List<Section> sections = sectionDao.findByLineId(lineId);
//        line.deleteSections(station);
        // DAO 삭제로직
    }
}
