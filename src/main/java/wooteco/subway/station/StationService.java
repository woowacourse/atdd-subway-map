package wooteco.subway.station;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station createStation(Station station) {
        long stationId = stationDao.save(station);
        return new Station(stationId, station.getName());
    }

    public List<Station> showStations() {
        return stationDao.findAll();
    }

    public void deleteStation(long id) {
        stationDao.delete(id);
    }
}
