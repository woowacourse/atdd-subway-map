package wooteco.subway.station;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import wooteco.subway.exception.DuplicateStationException;
import wooteco.subway.exception.IllegalInputException;
import wooteco.subway.exception.NoSuchStationException;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse createStation(String stationName) {
        try {
            long stationId = stationDao.save(stationName);
            return new StationResponse(stationId, stationName);
        } catch (DataAccessException e) {
            throw new DuplicateStationException();
        }
    }

    public List<Station> showStations() {
        return stationDao.findAll();
    }

    public void deleteStation(long id) {
        if (stationDao.delete(id) != 1) {
            throw new NoSuchStationException();
        }
    }

    public Station showStation(Long stationId) {
        try {
            return stationDao.findById(stationId);
        } catch (DataAccessException e) {
            throw new NoSuchStationException();
        }
    }
}
