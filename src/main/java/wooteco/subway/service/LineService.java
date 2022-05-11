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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
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
        final Line line = createLine(lineRequest);

        final Station upStation = findByStationId(lineRequest.getUpStationId());
        final Station downStation = findByStationId(lineRequest.getDownStationId());
        final List<StationResponse> stationResponses = makeStationResponseByStation(List.of(upStation, downStation));

        return new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses);
    }

    private void checkExistLineByName(LineRequest lineRequest) {
        if (lineDao.hasLine(lineRequest.getName())) {
            throw new IllegalArgumentException("같은 이름의 노선이 존재합니다.");
        }
    }

    private Line createLine(LineRequest lineRequest) {
        final Long id = lineDao.save(lineRequest);
        final Section section = saveInitialSection(id, lineRequest.getUpStationId(), lineRequest.getDownStationId(),
                lineRequest.getDistance());
        return new Line(id, lineRequest.getName(), lineRequest.getColor(), section);
    }

    private Section saveInitialSection(Long newLineId, Long upStationId, Long downStationId, int distance) {
        final Section section = new Section(newLineId, upStationId, downStationId, distance);
        final Long sectionId = sectionDao.save(section);
        return new Section(sectionId, newLineId, upStationId, downStationId, distance);
    }

    private Station findByStationId(Long id) {
        return stationDao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("구간 내 존재하는 역 조회에 오류가 발생했습니다."));
    }

    public void addSection(Long lineId, SectionRequest sectionRequest) {
        final Line line = loadLine(lineId);
        validateLine(sectionDao.findByLineId(lineId));

        final Section section = new Section(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
                sectionRequest.getDistance());
        line.addSection(section);
        checkFinalSectionAndAdd(lineId, section);
    }

    private void validateLine(List<Section> sections) {
        if (sections.isEmpty()) {
            throw new NoSuchElementException("구간이 존재하지 않는 노선입니다. 오류가 발생했습니다.");
        }
    }

    private void checkFinalSectionAndAdd(Long lineId, Section section) {
        final Optional<Section> upSection = sectionDao.findByDownStationId(lineId, section.getUpStationId());
        final Optional<Section> downSection = sectionDao.findByUpStationId(lineId, section.getDownStationId());
        if (upSection.isEmpty() && downSection.isEmpty()) {
            addNotFinalSection(lineId, section);
            return;
        }
        sectionDao.save(section);
    }

    private void addNotFinalSection(Long lineId, Section section) {
        final Section existSection = sectionDao.findBySameUpOrDownStationId(lineId, section)
                .orElseThrow(() -> new NoSuchElementException("구간이 존재하지 않습니다."));
        saveSplitSection(existSection, section);
        sectionDao.save(section);
    }

    private void saveSplitSection(Section existSection, Section section) {
        final int newDistance = existSection.getDistance() - section.getDistance();
        if (existSection.hasSameUpStation(section)) {
            sectionDao.updateUpStation(existSection.getId(), section.getDownStationId(), newDistance);
            return;
        }
        sectionDao.updateDownStation(existSection.getId(), section.getUpStationId(), newDistance);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        final List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(this::makeLineResponseByLine)
                .collect(Collectors.toUnmodifiableList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long id) {
        final Line line = loadLine(id);
        return makeLineResponseByLine(line);
    }

    private LineResponse makeLineResponseByLine(Line line) {
        final List<Section> sections = sectionDao.findByLineId(line.getId());
        final List<Station> stations = sortStations(line.getId(), sections);

        final List<StationResponse> stationResponses = makeStationResponseByStation(stations);
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses);
    }

    private List<Station> sortStations(Long lineId, List<Section> sections) {
        final List<Station> stations = new ArrayList<>();
        final Section upSection = sections.stream()
                .filter(it -> sectionDao.findByDownStationId(lineId, it.getUpStationId()).isEmpty())
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("구간의 상행역 종점 조회에 오류가 발생했습니다."));
        stationDao.findById(upSection.getUpStationId()).ifPresent(stations::add);
        addDownSectionToSort(lineId, upSection, stations);
        return stations;
    }

    private void addDownSectionToSort(Long lineId, Section upSection, final List<Station> stations) {
        stationDao.findById(upSection.getDownStationId()).ifPresent(stations::add);
        final Optional<Section> downSection = sectionDao.findByUpStationId(lineId, upSection.getDownStationId());
        downSection.ifPresent(section -> addDownSectionToSort(lineId, section, stations));
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
        final List<Section> sections = sectionDao.findByLineId(id);
        sectionDao.delete(sections);
        lineDao.deleteById(id);
    }

    private void checkExistLineById(Long id) {
        lineDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + "번에 해당하는 노선이 존재하지 않습니다."));
    }

    public void deleteSection(Long lineId, Long stationId) {
        final Station station = stationDao.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException(stationId + "번에 해당하는 지하철역이 존재하지 않습니다."));
        final Line line = loadLine(lineId);
        line.deleteSections(station);
        final Optional<Section> upSection = sectionDao.findByDownStationId(lineId, stationId);
        final Optional<Section> downSection = sectionDao.findByUpStationId(lineId, stationId);
        upSection.ifPresent((up -> downSection.ifPresent(down -> updateDownSectionByDeletion(up, down))));
        if (upSection.isEmpty() || downSection.isEmpty()) {
            upSection.ifPresent(deleteFinalSection());
            downSection.ifPresent(deleteFinalSection());
        }
    }

    private Line loadLine(Long lineId) {
        final Line findLine = lineDao.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("해당 노선이 존재하지 않습니다."));
        final List<Section> sections = sectionDao.findByLineId(lineId);
        return new Line(findLine.getId(), findLine.getName(), findLine.getColor(), sections);
    }

    private void updateDownSectionByDeletion(Section upSection, Section downSection) {
        sectionDao.delete(List.of(upSection));
        sectionDao.updateUpStation(downSection.getId(), upSection.getUpStationId(),
                upSection.getDistance() + downSection.getDistance());
    }

    private Consumer<Section> deleteFinalSection() {
        return section -> {
            sectionDao.delete(List.of(section));
        };
    }
}
