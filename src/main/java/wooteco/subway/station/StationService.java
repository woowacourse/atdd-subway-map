package wooteco.subway.station;

import org.springframework.stereotype.Service;
import wooteco.subway.station.exception.StationDeleteException;
import wooteco.subway.station.exception.StationExistenceException;

import java.util.List;

@Service
public class StationService {
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station create(StationRequest stationRequest) {
        if (isExistingStation(stationRequest.getName())) {
            throw new StationExistenceException();
        }
        return stationDao.save(stationRequest.getName());
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public void delete(Long id) {
        if (stationDao.delete(id) == 0) {
            throw new StationDeleteException();
        }
    }

    private boolean isExistingStation(String name) {
        return stationDao.findByName(name).isPresent();
    }
}
