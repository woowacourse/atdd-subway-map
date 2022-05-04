package wooteco.subway.application;

import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DuplicateException;
import wooteco.subway.exception.NotExistStationException;

public class StationService {

    public StationService() {
    }

    public Station saveByName(String name) {
        if (StationDao.existByName(name)) {
            throw new DuplicateException();
        }
        return StationDao.save(new Station(name));
    }

    public void deleteById(Long id) {
        if (!StationDao.existById(id)) {
            throw new NotExistStationException();
        }
        StationDao.deleteById(id);
    }
}
