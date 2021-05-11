package wooteco.subway.station;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.exception.StationError;
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

    public Station createStation(StationRequest stationRequest) {
        String stationName = stationRequest.getName();
        if (isStationExist(stationName)) {
            throw new StationException(StationError.ALREADY_EXIST_STATION_NAME);
        }
        return stationDao.save(stationName);
    }

    private boolean isStationExist(String name) {
        try {
            return stationDao.findByName(name)
                             .isPresent();
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new StationException(StationError.INCORRECT_SIZE_STATION_FIND_BY_ID);
        }
    }

    public void delete(Long id) {
        stationDao.delete(id);
    }
}
