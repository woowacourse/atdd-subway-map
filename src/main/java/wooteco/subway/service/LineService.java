package wooteco.subway.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.ui.dto.LineRequest;
import wooteco.subway.ui.dto.LineResponse;
import wooteco.subway.ui.dto.StationResponse;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public LineResponse save(LineRequest lineSaveRequest) {
        validateDuplicationName(lineSaveRequest.getName());
        Line line = new Line(lineSaveRequest.getName(), lineSaveRequest.getColor());
        Long savedId = lineDao.save(line);
        sectionDao.save(new Section(savedId, lineSaveRequest.getUpStationId(),
            lineSaveRequest.getDownStationId(), lineSaveRequest.getDistance()));

        return new LineResponse(savedId, line.getName(), line.getColor(), List.of(
            findStationByLineId(lineSaveRequest.getUpStationId()),
            findStationByLineId(lineSaveRequest.getDownStationId())
        ));
    }

    private void validateDuplicationName(String name) {
        if (lineDao.existsByName(name)) {
            throw new IllegalArgumentException("중복된 이름이 존재합니다.");
        }
    }

    public List<LineResponse> findAll() {
        Map<Long, Station> stations = findAllStations();
        return lineDao.findAll().stream()
            .map(i -> new LineResponse(i.getId(), i.getName(), i.getColor(),
                getSortedStationsByLineId(i.getId(), stations)))
            .collect(Collectors.toList());
    }

    private StationResponse findStationByLineId(Long lineId) {
        Station station = stationDao.findById(lineId);
        return new StationResponse(station.getId(), station.getName());
    }

    private Map<Long, Station> findAllStations() {
        return stationDao.findAll().stream()
            .collect(Collectors.toMap(Station::getId, i -> new Station(i.getName())));
    }

    private List<StationResponse> getSortedStationsByLineId(Long lineId, Map<Long, Station> stations) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        List<Long> stationIds = sections.sortedStationId();

        return stationIds.stream()
            .map(i -> toStationResponse(stations.get(i)))
            .collect(Collectors.toList());
    }

    private StationResponse toStationResponse(Station station) {
        return new StationResponse(station.getId(), station.getName());
    }

    private List<StationResponse> toStationResponse(List<Station> stations) {
        return stations.stream()
            .map(i -> new StationResponse(i.getId(), i.getName()))
            .collect(Collectors.toList());
    }

    public boolean deleteById(Long id) {
        return lineDao.deleteById(id);
    }

    public boolean updateById(Long id, Line line) {
        return lineDao.updateById(id, line);
    }

    public LineResponse findById(Long id) {
        Optional<Line> maybeLine = lineDao.findById(id);
        if (maybeLine.isEmpty()) {
            throw new IllegalArgumentException("Id에 해당하는 노선이 존재하지 않습니다.");
        }
        Line line = maybeLine.get();
        List<Station> stations = findSortedStationByLineId(line.getId());
        return new LineResponse(line.getId(), line.getName(), line.getColor(), toStationResponse(stations));
    }

    private List<Station> findSortedStationByLineId(Long lineId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        List<Long> stationIds = sections.sortedStationId();
        return stationIds.stream()
            .map(stationDao::findById)
            .collect(Collectors.toList());
    }
}
