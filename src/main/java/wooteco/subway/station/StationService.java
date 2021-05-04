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

    public StationResponse createStation(String name) {
        return new StationResponse(stationDao.insert(name));
    }

    public List<StationResponse> showStations() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        Station station = stationDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 이름의 역이 존재하지 않습니다."));
        StationDao.delete(station);
    }
}
