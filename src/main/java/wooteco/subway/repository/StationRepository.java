package wooteco.subway.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

@Repository
public class StationRepository {

    private final StationDao stationDao;

    public StationRepository(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Optional<Station> findStationById(Long stationId) {
        return stationDao.findById(stationId);
    }

    public List<Station> findStationsByIds(List<Long> stationIds) {
        return stationDao.findByIds(stationIds);
    }
}
