package wooteco.subway.station;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse createStation(final StationRequest stationRequest) {
        final Station station = stationDao.save(stationRequest.toEntity());
        return new StationResponse(station.getId(), station.getName());
    }

    public List<StationResponse> findStations() {
        final List<Station> stations = stationDao.findAll();
        return stations.stream()
            .map(station -> new StationResponse(station.getId(), station.getName()))
            .collect(Collectors.toList());
    }

    public void deleteStation(final Long id) {
        stationDao.deleteById(id);
    }
}
