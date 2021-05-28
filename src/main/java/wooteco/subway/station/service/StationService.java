package wooteco.subway.station.service;

import org.springframework.stereotype.Service;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.exception.StationError;
import wooteco.subway.station.exception.StationException;

import java.util.List;

@Service
public class StationService {
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public Station findById(Long id) {
        return stationDao.findById(id)
                .orElseThrow(() -> new StationException(StationError.NO_STATION_BY_ID));
    }

    public Station createStation(StationRequest stationRequest) {
        String stationName = stationRequest.getName();
        if (isStationExist(stationName)) {
            throw new StationException(StationError.ALREADY_EXIST_STATION_NAME);
        }
        return stationDao.save(stationName);
    }

    private boolean isStationExist(String name) {
        return stationDao.findByName(name)
                         .isPresent();
    }

    public void delete(Long id) {
        stationDao.delete(id);
    }
}
