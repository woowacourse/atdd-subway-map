package wooteco.subway.station;

import org.springframework.stereotype.Service;
import wooteco.subway.exception.station.StationNameDuplicatedException;
import wooteco.subway.station.dao.StationDao;

import java.util.List;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station create(String name) {
        if (stationDao.isExistByName(name)) {
            throw new StationNameDuplicatedException();
        }
        Station station = Station.of(name);
        return stationDao.save(station);
    }

    public List<Station> showAll() {
        return stationDao.findAll();
    }

    public void remove(Long id) {
        stationDao.remove(id);
    }

}
