package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.NameDuplicationException;

import java.util.List;

@Service
public class StationService {
    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station create(final String name) {
        checkDuplication(name);
        return stationDao.save(new Station(name));
    }

    private void checkDuplication(final String name) {
        if (stationDao.counts(name) > 0) {
            throw new NameDuplicationException();
        }
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public void delete(final Long id) {
        stationDao.deleteById(id);
    }

    public Station findById(final Long id) {
        return stationDao.findById(id);
    }
}
