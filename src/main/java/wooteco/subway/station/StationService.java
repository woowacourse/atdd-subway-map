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

    public StationResponse save(Station station) {
        if (stationNameExists(station.getName())) {
            throw new IllegalArgumentException("역 이름이 이미 존재합니다.");
        }
        Station newStation = stationDao.save(station);
        return new StationResponse(newStation.getId(), newStation.getName());
    }

    private boolean stationNameExists(String name) {
        return stationDao.count(name) > 0;
    }

    public List<StationResponse> findAll() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        if (stationIdNotExists(id)) {
            throw new IllegalArgumentException("역 ID가 존재하지 않습니다.");
        }
        stationDao.delete(id);
    }

    private boolean stationIdNotExists(Long id) {
        return stationDao.count(id) == 0;
    }
}
