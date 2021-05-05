package wooteco.subway.station;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse createStation(String stationName) {
        Optional<Station> duplicateStation = stationDao.findByName(stationName);
        if (duplicateStation.isPresent()) {
            throw new IllegalArgumentException("중복된 역이 존재합니다.");
        }

        Station station = new Station(stationName);
        Station newStation = stationDao.save(station);
        return new StationResponse(newStation.getId(), newStation.getName());
    }

    public List<StationResponse> showStations() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
    }

    public void deleteStation(Long id) {
        Optional<Station> station = stationDao.findById(id);
        if(!station.isPresent()) {
            throw new IllegalArgumentException("삭제할 역이 존재하지 않습니다.");
        }
        stationDao.delete(station.get());
    }
}
