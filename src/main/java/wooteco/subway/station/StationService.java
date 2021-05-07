package wooteco.subway.station;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse createStation(StationRequest stationRequest) {
        StationEntity stationEntity = new StationEntity(stationRequest.getName());
        StationEntity newStationEntity = stationDao.save(stationEntity);
        return new StationResponse(newStationEntity.getId(), newStationEntity.getName());
    }

    public List<StationResponse> showStations() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
            .map(it -> new StationResponse(it.getId(), it.getName()))
            .collect(Collectors.toList());
    }

    public void delete(Long id) {
        stationDao.delete(id);
    }
}
