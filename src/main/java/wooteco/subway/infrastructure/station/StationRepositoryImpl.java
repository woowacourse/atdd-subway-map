package wooteco.subway.infrastructure.station;

import org.springframework.stereotype.Repository;
import wooteco.subway.domain.station.Station;
import wooteco.subway.domain.station.StationRepository;
import wooteco.subway.exception.station.ReferencedStationException;

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
    public Station findById(Long id) {
        return stationDao.findById(id);
    }

    @Override
    public List<Station> findAll() {
        return stationDao.findAll();
    }

    //is it possible??
    @Override
    public int delete(Long id) {
        if(stationDao.isReferenced(id)) {
            throw new ReferencedStationException();
        }

        return stationDao.delete(id);
    }

    @Override
    public boolean contains(Long id) {
        return stationDao.contains(id);
    }

}
