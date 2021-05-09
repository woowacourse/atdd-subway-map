package wooteco.subway.station;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse createStation(String stationName) {
        Station station = stationDao.save(stationName);
        return StationResponse.from(station);
    }

    public List<StationResponse> showStations() {
        final List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }

    public void deleteStation(long id) {
        stationDao.delete(id);
    }
}
