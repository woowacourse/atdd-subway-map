package wooteco.subway.station.repository;

import org.springframework.stereotype.Repository;
import wooteco.subway.exception.badrequest.StationNotFoundException;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.infra.StationDao;

import java.util.List;

@Repository
public class StationRepositoryImpl implements StationRepository {

    private final StationDao stationDao;

    public StationRepositoryImpl(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Override
    public Station save(final Station station) {
        return stationDao.save(station);
    }

    @Override
    public Station findById(final Long id) {
        return stationDao.findById(id).orElseThrow(StationNotFoundException::new);
    }

    @Override
    public List<Station> findByIds(List<Long> ids) {
        return stationDao.findByIds(ids);
    }

    @Override
    public List<Station> findAll() {
        return stationDao.findAll();
    }

    @Override
    public void delete(final Long id) {
        stationDao.delete(id);
    }

    @Override
    public void deleteAll() {
        stationDao.deleteAll();
    }
}
