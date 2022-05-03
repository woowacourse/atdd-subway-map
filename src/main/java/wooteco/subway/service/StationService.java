package wooteco.subway.service;

import wooteco.subway.repository.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.repository.entity.StationEntity;

public class StationService {

    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station register(final String name) {
        final Station station = new Station(name);
        final StationEntity entity = stationDao.save(station);
        return new Station(entity.getId(), entity.getName());
    }
}
