package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;

@Service
public class StationService {
    private static final String ALREADY_IN_STATION_ERROR_MESSAGE = "이미 해당 이름의 역이 있습니다.";
    private static final String NO_STATION_ID_ERROR_MESSAGE = "해당 아이디의 역이 없습니다.";
    private static final String ALREADY_IN_LINE_ERROR_MESSAGE = "지하철 노선에 해당 역이 등록되어있어 역을 삭제할 수 없습니다.";

    private final SectionDao sectionDao;
    private final StationDao stationDao;
    private final LineDao lineDao;

    public StationService(SectionDao sectionDao, StationDao stationDao, LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
        this.lineDao = lineDao;
    }

    public Station save(Station station) {
        validateUniqueName(station.getName());
        Long id = stationDao.save(station);
        return stationDao.findById(id);
    }

    private void validateUniqueName(String name) {
        if (stationDao.hasStation(name)) {
            throw new IllegalArgumentException(ALREADY_IN_STATION_ERROR_MESSAGE);
        }
    }

    public Station findById(Long id) {
        checkStationExist(id);
        return stationDao.findById(id);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public void delete(Long id) {
        checkStationExist(id);
        validateStationNotLinked(id);
        stationDao.delete(id);
    }

    private void validateStationNotLinked(Long stationId) {
        lineDao.findAll().stream()
                .map(this::getSections)
                .filter(sections -> !sections.findByStation(stationDao.findById(stationId)).isEmpty())
                .findAny()
                .ifPresent(section -> {throw new IllegalArgumentException(ALREADY_IN_LINE_ERROR_MESSAGE);});
    }

    private Sections getSections(Line line) {
        return new Sections(sectionDao.findAllByLineId(line.getId()));
    }

    private void checkStationExist(Long id) {
        if (!stationDao.hasStation(id)) {
            throw new IllegalArgumentException(NO_STATION_ID_ERROR_MESSAGE);
        }
    }
}
