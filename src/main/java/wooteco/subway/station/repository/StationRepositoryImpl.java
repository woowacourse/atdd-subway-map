package wooteco.subway.station.repository;

import org.springframework.stereotype.Repository;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.StationRepository;

import java.util.List;

@Repository
public class StationRepositoryImpl implements StationRepository {

    private final StationDao stationDao;

    public StationRepositoryImpl(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Override
    public Station save(final Station station) {
        return stationDao.save(station);
    }

    @Override
    public List<Station> findAll() {
        return stationDao.findAll();
    }

    @Override
    public int delete(Long id) {
        return stationDao.delete(id);
    }
}
