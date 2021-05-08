package wooteco.subway.repository;

import org.springframework.stereotype.Repository;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

import java.util.Optional;

@Repository
public class StationRepository {
    private final StationDao stationDao;

    public StationRepository(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Optional<Station> findStationById(Long stationId) {
        return stationDao.findById(stationId);
    }
}
