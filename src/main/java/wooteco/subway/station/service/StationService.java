package wooteco.subway.station.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.station.api.dto.StationRequest;
import wooteco.subway.station.api.dto.StationResponse;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.model.Station;

import java.util.List;

@Service
@Transactional
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse createStation(StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        long createdId = stationDao.save(station);
        Station newStation = stationDao.findStationById(createdId);
        return new StationResponse(newStation.getId(), newStation.getName());
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAll() {
        List<Station> stations = stationDao.findAll();
        return StationResponse.listOf(stations);
    }

    public void deleteById(Long id) {
        stationDao.deleteById(id);
    }
}
