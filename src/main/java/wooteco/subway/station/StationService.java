package wooteco.subway.station;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station createStation(String stationName) {
        long stationId = stationDao.save(stationName);
        return new Station(stationId, stationName);
    }

    public List<Station> showStations() {
        return stationDao.findAll();
    }

    public void deleteStation(long id) {
        stationDao.delete(id);
    }
}
