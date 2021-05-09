package wooteco.subway.station;

import org.springframework.stereotype.Service;

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

    private boolean isExistingStation(String name) {
        return stationDao.findByName(name).isPresent();
    }

    public void delete(Long id) {
        stationDao.delete(id);
    }
}