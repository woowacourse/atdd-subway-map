package wooteco.subway.station;

import java.util.List;

public class StationService {
    private final StationDao stationDao;

    public StationService() {
        this.stationDao = new StationDao();
    }

    public Station createStation(String stationName) {
        if (isStationExist(stationName)) {
            throw new IllegalArgumentException();
        }
        return stationDao.save(new Station(stationName));
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    private boolean isStationExist(String stationName) {
        return stationDao.findAll()
                         .stream()
                         .anyMatch(station -> stationName.equals(station.getName()));
    }
}
