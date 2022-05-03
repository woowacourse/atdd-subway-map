package wooteco.subway.service;

import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DuplicatedStationNameException;
import wooteco.subway.exception.StationNotFoundException;

public class StationService {

    public static Station save(Station station) {
        if (StationDao.exists(station)) {
            throw new DuplicatedStationNameException();
        }
        return StationDao.save(station);
    }

    public static void deleteById(long id) {
        if (StationDao.findById(id).isEmpty()) {
            throw new StationNotFoundException();
        }
        StationDao.deleteById(id);
    }
}
