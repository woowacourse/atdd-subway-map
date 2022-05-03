package wooteco.subway.service;

import java.util.List;

import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station save(Station station) {
        return stationDao.save(station);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }
}
