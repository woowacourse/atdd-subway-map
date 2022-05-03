package wooteco.subway.service;

import java.util.List;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

public class StationService {

    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station save(final Station station) {
        return stationDao.save(station);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public void delete(final Long stationId) {
        stationDao.delete(stationId);
    }
}
