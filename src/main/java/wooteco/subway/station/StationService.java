package wooteco.subway.station;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
    public StationResponse save(Station station) {
        if (stationDao.existsByName(station.getName())) {
            throw new IllegalArgumentException("역 이름이 이미 존재합니다.");
        }
        Station newStation = stationDao.save(station);
        return new StationResponse(newStation.getId(), newStation.getName());
    }

    @Transactional
    public List<StationResponse> findAll() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        if (!stationDao.findById(id).isPresent()) {
            throw new IllegalArgumentException("역 ID가 존재하지 않습니다.");
        }
        stationDao.delete(id);
    }
}
