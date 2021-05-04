package wooteco.subway.station;

import java.util.List;

public class StationService {
    private final StationDao stationDao;

    public StationService() {
        this.stationDao = new StationDao();
    }

    public Station createStation(String name) {
        if (isStationExist(name)) {
            throw new IllegalArgumentException("존재하는 역 이름입니다.");
        }
        return stationDao.save(new Station(name));
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    private boolean isStationExist(String name) {
        return stationDao.findAll()
                         .stream()
                         .anyMatch(station -> name.equals(station.getName()));
    }

    public void delete(Long id) {
        stationDao.delete(id);
    }
}
