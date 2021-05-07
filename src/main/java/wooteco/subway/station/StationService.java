package wooteco.subway.station;

import org.springframework.stereotype.Service;
import wooteco.subway.station.exception.ErrorCode;
import wooteco.subway.station.exception.StationException;

import java.util.List;

@Service
public class StationService {
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station createStation(String name) {
        if (isStationExist(name)) {
            throw new StationException(ErrorCode.ALREADY_EXIST_STATION_NAME);
        }
        return stationDao.save(name);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    private boolean isStationExist(String name) {
        return stationDao.findByName(name).isPresent();
    }

    public void delete(Long id) {
        stationDao.delete(id);
    }
}
