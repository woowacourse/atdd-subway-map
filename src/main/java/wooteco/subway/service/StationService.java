package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

@Service
public class StationService {
    private static final String ALREADY_IN_STATION_ERROR_MESSAGE = "이미 해당 이름의 역이 있습니다.";
    private static final String NO_ID_ERROR_MESSAGE = "해당 아이디의 역이 없습니다.";

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station save(Station station) {
        validateUniqueName(station.getName());
        Long id = stationDao.save(station);
        return stationDao.findById(id);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public void delete(Long id) {
        validateID(id);
        stationDao.delete(id);
    }

    private void validateUniqueName(String name) {
        if (stationDao.hasStation(name)) {
            throw new IllegalArgumentException(ALREADY_IN_STATION_ERROR_MESSAGE);
        }
    }

    private void validateID(Long id) {
        if (!stationDao.hasStation(id)) {
            throw new IllegalArgumentException(NO_ID_ERROR_MESSAGE);
        }
    }
}
