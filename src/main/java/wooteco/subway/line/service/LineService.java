package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.dto.SectionEntity;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.dao.SectionDao;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.Section;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.dto.SectionRequest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public Line save(final Line line) {
        validateDuplicate(line);
        return lineDao.save(line);
    }

    private void validateDuplicate(final Line line) {
        if (lineDao.findByName(line.name()).isPresent()) {
            throw new IllegalStateException("[ERROR] 이미 존재하는 노선입니다.");
        }
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(final Long id) {
        return lineDao.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("[ERROR] 존재하지 않는 노선입니다."));
    }

    public Line findByName(final String name) {
        return lineDao.findByName(name)
                .orElseThrow(() ->
                        new IllegalArgumentException("[ERROR] 존재하지 않는 노선입니다."));
    }

    public void update(final Long id, String name, String color) {
        lineDao.update(new Line(id, name, color));
    }

    public void delete(final Long id) {
        lineDao.delete(id);
    }

    @Transactional
    public LineResponse save(final LineRequest lineRequest) {
        Line savedLine = lineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        List<Station> stations = Arrays.asList(
                findStationById(lineRequest.getUpStationId()),
                findStationById(lineRequest.getDownStationId()));
        sectionDao.save(new SectionEntity(savedLine.id(), lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance()));
        return new LineResponse(savedLine.id(), savedLine.name(), savedLine.color(), stations);
    }

    private Station findStationById(Long stationId) {
        return stationDao.findById(stationId)
                .orElseThrow(() -> new IllegalStateException("[ERROR] 존재하지 않는 역입니다."));
    }

    @Transactional
    public void addSection(final Long lineId, final SectionRequest sectionRequest) {
        Line line = findLineById(lineId);
        List<Section> sections = findSectionsByLineId(lineId);
        line.initSections(sections);

        //
        line.addSection(new Section(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance()));

        // TODO : 예외
        //  lineId가 존재하는지
        //  line의 section에 upstationId와 downStationId 둘다 존재하는지 - 노선의 구간에 이미 등록되어있음
        //  upstationId 또는 downStationId로 section을 찾는데, 찾은 section의 distance가 sectionAddRequest의 distance보다 작거나 같은 경우


        // TODO : line의 section에 sectionAddRequest의 upstationId가 존재하는지
        //  존재하면 sectionAddRequest의 upstationId로 section을 찾고
        //  찾은 section의 upstationId를 sectionAddRequest의 downStationId로 수정한다.
        //  찾은 section의 distance를 sectionAddRequest의 distance를 뺀 값으로 수정한다.
        //

        // TODO : line의 section에 sectionAddRequest의 downStationId가 존재하는지
        //  존재하면 sectionAddRequest의 downStationId로 section을 찾고
        //  찾은 section의 downStationId를 sectionAddRequest의 upStationId로 수정한다.
        //  찾은 section의 distance를 sectionAddRequest의 distance를 뺀 값으로 수정한다.

        // TODO : section save

        sectionDao.save(sectionRequest.toEntity(lineId));
    }

    private List<Section> findSectionsByLineId(Long lineId) {
        return sectionDao.findAllByLineId(lineId)
                .stream()
                .map(section ->
                        new Section(section.getId(), findStationById(section.getUpStationId()),
                                findStationById(section.getDownStationId()), section.getDistance()))
                .collect(Collectors.toList());
    }

    private Line findLineById(Long lineId) {
        return lineDao.findById(lineId)
                .orElseThrow(() -> new IllegalStateException("[ERROR] 존재하지 않는 노선입니다."));
    }
}
