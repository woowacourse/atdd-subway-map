package wooteco.subway.station;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse createStation(String stationName) {
        long stationId = stationDao.save(stationName);
        return new StationResponse(stationId, stationName);
    }

    public List<Station> showStations() {
        return stationDao.findAll();
    }

    public void deleteStation(long id) {
        stationDao.delete(id);
    }
}
