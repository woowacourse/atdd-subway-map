package wooteco.subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DuplicateException;
import wooteco.subway.exception.NotExistException;

@Service
@Transactional
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station save(String name) {
        if (stationDao.existByName(name)) {
            throw new DuplicateException();
        }
        return stationDao.save(new Station(name));
    }

    public void deleteById(Long id) {
        if (!stationDao.existById(id)) {
            throw new NotExistException();
        }
        stationDao.deleteById(id);
    }
}
