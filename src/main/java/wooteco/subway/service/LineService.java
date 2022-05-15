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
import wooteco.subway.service.dto.LineServiceRequest;
import wooteco.subway.service.dto.LineServiceResponse;
import wooteco.subway.service.dto.StationServiceResponse;

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
    public LineServiceResponse save(LineServiceRequest lineServiceRequest) {
        validateDuplicationName(lineServiceRequest.getName());
        Line line = new Line(lineServiceRequest.getName(), lineServiceRequest.getColor());
        Long savedId = lineDao.save(line);
        sectionDao.save(new Section(savedId, lineServiceRequest.getUpStationId(),
            lineServiceRequest.getDownStationId(), lineServiceRequest.getDistance()));

        return new LineServiceResponse(savedId, line.getName(), line.getColor(), List.of(
            findStationByLineId(lineServiceRequest.getUpStationId()),
            findStationByLineId(lineServiceRequest.getDownStationId())
        ));
    }

    private void validateDuplicationName(String name) {
        if (lineDao.existsByName(name)) {
            throw new IllegalArgumentException("중복된 이름이 존재합니다.");
        }
    }

    public List<LineServiceResponse> findAll() {
        Map<Long, Station> stations = findAllStations();
        return lineDao.findAll().stream()
            .map(i -> new LineServiceResponse(i.getId(), i.getName(), i.getColor(),
                getSortedStationsByLineId(i.getId(), stations)))
            .collect(Collectors.toList());
    }

    private StationServiceResponse findStationByLineId(Long lineId) {
        Station station = stationDao.findById(lineId);
        return new StationServiceResponse(station.getId(), station.getName());
    }

    private Map<Long, Station> findAllStations() {
        return stationDao.findAll().stream()
            .collect(Collectors.toMap(Station::getId, i -> new Station(i.getName())));
    }

    private List<StationServiceResponse> getSortedStationsByLineId(Long lineId,
        Map<Long, Station> stations) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        List<Long> stationIds = sections.sortedStationId();

        return stationIds.stream()
            .map(i -> toStationResponse(stations.get(i)))
            .collect(Collectors.toList());
    }

    private StationServiceResponse toStationResponse(Station station) {
        return new StationServiceResponse(station.getId(), station.getName());
    }

    private List<StationServiceResponse> toStationResponse(List<Station> stations) {
        return stations.stream()
            .map(i -> new StationServiceResponse(i.getId(), i.getName()))
            .collect(Collectors.toList());
    }

    public boolean deleteById(Long id) {
        return lineDao.deleteById(id);
    }

    public boolean updateById(Long id, LineServiceRequest lineServiceRequest) {
        Line line = new Line(lineServiceRequest.getName(), lineServiceRequest.getColor());
        return lineDao.updateById(id, line);
    }

    public LineServiceResponse findById(Long id) {
        Optional<Line> maybeLine = lineDao.findById(id);
        if (maybeLine.isEmpty()) {
            throw new IllegalArgumentException("Id에 해당하는 노선이 존재하지 않습니다.");
        }
        Line line = maybeLine.get();
        List<Station> stations = findSortedStationByLineId(line.getId());
        return new LineServiceResponse(line.getId(), line.getName(), line.getColor(),
            toStationResponse(stations));
    }

    private List<Station> findSortedStationByLineId(Long lineId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        List<Long> stationIds = sections.sortedStationId();
        return stationIds.stream()
            .map(stationDao::findById)
            .collect(Collectors.toList());
    }
}
