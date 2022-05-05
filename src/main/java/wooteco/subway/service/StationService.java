package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.station.DuplicatedStationException;
import wooteco.subway.exception.station.StationNotFoundException;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station save(Station station) {
        if (stationDao.exists(station)) {
            throw new DuplicatedStationException();
        }
        return stationDao.save(station);
    }

    public void deleteById(Long id) {
        if (!stationDao.exists(id)) {
            throw new StationNotFoundException();
        }
        stationDao.deleteById(id);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }
}
