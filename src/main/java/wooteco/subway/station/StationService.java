package wooteco.subway.station;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import wooteco.subway.station.exception.ErrorCode;
import wooteco.subway.station.exception.StationException;

import java.util.List;

@Service
public class StationService {
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public Station createStation(String name) {
        if (isStationExist(name)) {
            throw new StationException(ErrorCode.ALREADY_EXIST_STATION_NAME);
        }
        return stationDao.save(name);
    }

    private boolean isStationExist(String name) {
        try {
            return stationDao.findByName(name)
                             .isPresent();
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new StationException(ErrorCode.INCORRECT_SIZE_STATION_FIND_BY_ID);
        }
    }

    public void delete(Long id) {
        stationDao.delete(id);
    }
}
