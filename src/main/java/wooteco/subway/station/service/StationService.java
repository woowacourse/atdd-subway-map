package wooteco.subway.station.service;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import wooteco.subway.exception.duplicate.DuplicateStationException;
import wooteco.subway.exception.nosuch.NoSuchStationException;
import wooteco.subway.station.Station;
import wooteco.subway.station.dao.StationDao;

@Service
@Transactional
@Validated
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station createStation(Station station) {
        try {
            long stationId = stationDao.save(station);
            return new Station(stationId, station);
        } catch (DataAccessException e) {
            throw new DuplicateStationException();
        }
    }

    public List<Station> showStations() {
        return stationDao.findAll();
    }

    public void deleteStation(long id) {
        if (stationDao.delete(id) != 1) {
            throw new NoSuchStationException();
        }
    }

    public Station showStation(Long stationId) {
        return stationDao.findById(stationId).orElseThrow(NoSuchStationException::new);
    }

}
