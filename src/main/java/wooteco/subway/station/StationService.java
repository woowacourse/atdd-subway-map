package wooteco.subway.station;

import java.util.List;

public class StationService {
    private StationDao stationDao;

    public StationService() {
        stationDao = new StationDao();
    }

    public Station save(String stationName) {
        Station station = new Station(stationName);
        if (stationDao.find(stationName).isPresent()) {
            throw new IllegalArgumentException("같은 이름의 역이 있습니다;");
        }
        return stationDao.save(station);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public void delete(Long id) {
        stationDao.delete(id);
    }
}
