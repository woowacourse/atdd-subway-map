package wooteco.subway.station;

import org.springframework.stereotype.Service;
import wooteco.subway.station.exception.StationExistenceException;
import wooteco.subway.station.exception.StationNotFoundException;

import java.util.List;

@Service
public class StationService {
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station createStation(StationRequest stationRequest) {
        if (isExistingStation(stationRequest.getName())) {
            throw new StationExistenceException();
        }
        return stationDao.save(stationRequest.getName());
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public void deleteStation(Long id) {
        if (!isExistingStation(id)) {
            throw new StationNotFoundException();
        }
        stationDao.delete(id);
    }

    private boolean isExistingStation(String name) {
        return stationDao.findByName(name).isPresent();
    }

    private boolean isExistingStation(Long id) {
        return stationDao.findById(id).isPresent();
    }
}
