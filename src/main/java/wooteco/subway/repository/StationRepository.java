package wooteco.subway.repository;

import java.util.List;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

@Repository
public class StationRepository {

    private final StationDao stationDao;

    public StationRepository(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station save(Station station) {
        return stationDao.save(station);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public Station findById(Long id) {
        return stationDao.findById(id);
    }

    public void deleteById(Long id) {
        stationDao.deleteById(id);
    }
}
