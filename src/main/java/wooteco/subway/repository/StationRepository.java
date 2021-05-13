package wooteco.subway.repository;

import org.springframework.stereotype.Repository;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.station.Station;

import java.util.List;
import java.util.Optional;

@Repository
public class StationRepository {

    private final StationDao stationDao;

    public StationRepository(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public long save(Station station) {
        return stationDao.save(station);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public Optional<Station> findById(long id) {
        return stationDao.findById(id);
    }

    public void deleteById(long id) {
        stationDao.deleteById(id);
    }
}
