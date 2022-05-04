package wooteco.subway.service;

import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DuplicatedStationException;
import wooteco.subway.exception.StationNotFoundException;

public class StationService {

    public static Station save(Station station) {
        if (StationDao.exists(station)) {
            throw new DuplicatedStationException();
        }
        return stationDao.save(station);
    }

    public void deleteById(long id) {
        int executionResult = stationDao.deleteById(id);
        if (executionResult == 0) {
            throw new StationNotFoundException();
        }
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }
}
