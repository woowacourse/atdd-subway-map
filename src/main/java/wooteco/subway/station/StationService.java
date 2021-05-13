package wooteco.subway.station;

import org.springframework.stereotype.Service;

@Service
public class StationService {

    private StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }


    public StationResponse save(Station station) {
        long id = stationDao.save(station);
        return new StationResponse(id, station.getName());
    }
}
