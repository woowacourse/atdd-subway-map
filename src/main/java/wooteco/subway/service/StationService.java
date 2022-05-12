package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.station.StationResponse;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse createStation(String name) {
        return stationDao.save(new Station(name));
    }

    public void deleteStation(Long id) {
        stationDao.deleteById(id);
    }

    public List<StationResponse> findAll() {
        return stationDao.findAll();
    }
}
