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
    public Station findById(final Long id) {
        return stationDao.findById(id);
    }

    @Override
    public void delete(final Long id) {
        stationDao.deleteById(id);
    }

    @Override
    public List<Station> findAll() {
        return stationDao.findAll();
    }
}
