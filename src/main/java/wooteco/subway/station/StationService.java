package wooteco.subway.station;

import java.util.List;

public class StationService {
    private StationDao stationDao;

    public StationService() {
        stationDao = new StationDao();
    }
    public Station save(String stationName) {
        Station station = new Station(stationName);
        return stationDao.save(station);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public void delete(Long id) {
        stationDao.delete(id);
    }
}
