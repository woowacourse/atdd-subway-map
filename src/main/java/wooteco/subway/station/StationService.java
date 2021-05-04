package wooteco.subway.station;

import java.util.List;

public class StationService {

    private static StationService instance;

    private StationService() {

    }

    public static StationService getInstance() {
        if (instance == null) {
            instance = new StationService();
        }
        return instance;
    }

    public Station add(String name) {
        return StationDao.save(new Station(name));
    }

    public List<Station> stations() {
        return StationDao.findAll();
    }

    public void delete(Long id) {
        StationDao.delete(id);
    }
}
