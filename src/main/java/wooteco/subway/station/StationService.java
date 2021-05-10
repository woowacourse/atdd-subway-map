package wooteco.subway.station;

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
        final Optional<Station> stationWithSameName = stationDao.findStationByName(stationName);
        if (stationWithSameName.isPresent()) {
            throw new IllegalArgumentException("역 이름이 중복됩니다");
        }
        Station station = stationDao.save(stationName);
        return StationResponse.from(station);
    }

    public List<StationResponse> showStations() {
        final List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }

    public void deleteStation(long stationId) {
        final Optional<Station> stationFoundById = stationDao.findById(stationId);
        if (!stationFoundById.isPresent()) {
            throw new IllegalArgumentException("해당 id에 대응하는 역이 없습니다.");
        }
        Station station = stationFoundById.get();
        stationDao.delete(station.getId());
    }
}
