package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

@Service
public class StationService {
    private static final String ALREADY_IN_STATION_ERROR_MESSAGE = "이미 해당 이름의 역이 있습니다.";
    private static final String NO_STATION_ID_ERROR_MESSAGE = "해당 아이디의 역이 없습니다.";

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
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

    private void checkStationExist(Long id) {
        if (!stationDao.hasStation(id)) {
            throw new IllegalArgumentException(NO_STATION_ID_ERROR_MESSAGE);
        }
    }
}
