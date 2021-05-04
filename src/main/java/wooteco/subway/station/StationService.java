package wooteco.subway.station;

import java.util.List;

public class StationService {

    private final StationDao stationDao;
    private static StationService instance;

    private StationService() {
        this.stationDao = StationDao.getInstance();
    }

    public static StationService getInstance() {
        if (instance == null) {
            instance = new StationService();
        }
        return instance;
    }

    public Station add(String name) {
        return stationDao.save(new Station(name));
    }

    public List<Station> stations() {
        return stationDao.findAll();
    }

    public void delete(Long id) {
        stationDao.delete(id);
    }
}
