package wooteco.subway.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.service.dto.line.LineFindResponse;
import wooteco.subway.service.dto.line.LineSaveRequest;
import wooteco.subway.service.dto.line.LineSaveResponse;
import wooteco.subway.ui.dto.StationResponse;

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

    public LineSaveResponse save(LineSaveRequest lineSaveRequest) {
        validateDuplicationName(lineSaveRequest.getName());
        Line line = new Line(lineSaveRequest.getName(), lineSaveRequest.getColor());
        Long savedId = lineDao.save(line);
        sectionDao.save(new Section(savedId, lineSaveRequest.getUpStationId(),
            lineSaveRequest.getDownStationId(), lineSaveRequest.getDistance()));

        return new LineSaveResponse(savedId, line.getName(), line.getColor(), List.of(
            findStationByLineId(lineSaveRequest.getUpStationId()),
            findStationByLineId(lineSaveRequest.getDownStationId())
        ));
    }

    private void validateDuplicationName(String name) {
        if (lineDao.existsByName(name)) {
            throw new IllegalArgumentException("중복된 이름이 존재합니다.");
        }
    }

    public List<LineFindResponse> findAll() {
        Map<Long, Station> stations = findAllStations();
        return lineDao.findAll().stream()
            .map(i -> new LineFindResponse(i.getId(), i.getName(), i.getColor(),
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

    private List<Station> getSortedStationsByLineId(Long lineId, Map<Long, Station> stations) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        List<Long> stationIds = sections.sortedStationId();

        return stationIds.stream()
            .map(stations::get)
            .collect(Collectors.toList());
    }

    public boolean deleteById(Long id) {
        return lineDao.deleteById(id);
    }

    public boolean updateById(Long id, Line line) {
        return lineDao.updateById(id, line);
    }

    public LineFindResponse findById(Long id) {
        Line line = lineDao.findById(id).get();
        List<Station> stations = findSortedStationByLineId(line.getId());
        return new LineFindResponse(line.getId(), line.getName(), line.getColor(), stations);
    }

    private List<Station> findSortedStationByLineId(Long lineId) {
        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        List<Long> stationIds = sections.sortedStationId();
        return stationIds.stream()
            .map(stationDao::findById)
            .collect(Collectors.toList());
    }
}
