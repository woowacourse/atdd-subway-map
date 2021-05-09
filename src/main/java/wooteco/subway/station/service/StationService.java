package wooteco.subway.station.service;

import org.springframework.stereotype.Service;
import wooteco.subway.exception.StationNameDuplicatedException;
import wooteco.subway.station.Station;
import wooteco.subway.station.service.dao.StationDao;

import java.util.List;
import java.util.Optional;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public Optional<Station> findStationByName(String name) {
        return stationDao.findStationByName(name);
    }

    public Station save(Station station) {
        if (stationDao.findStationByName(station.getName()).isPresent()) {
            throw new StationNameDuplicatedException();
        }
        return stationDao.save(station);
    }

    public void remove(Long id) {
        stationDao.remove(id);
    }
}
