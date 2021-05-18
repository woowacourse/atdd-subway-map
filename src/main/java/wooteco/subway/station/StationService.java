package wooteco.subway.station;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.exception.DataNotFoundException;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse createStation(final StationRequest stationRequest) {
        final Station station = stationDao.save(stationRequest.toEntity());
        return StationResponse.of(station.getId(), station.getName());
    }

    public List<StationResponse> findStations() {
        final List<Station> stations = stationDao.findAll();
        return stations.stream()
            .map(station -> StationResponse.of(station.getId(), station.getName()))
            .collect(Collectors.toList());
    }

    public Stations findByIds(final List<Long> ids) {
        final List<Station> stationGroup = stationDao.findByIds(ids);
        if (stationGroup.size() != ids.size()) {
            throw new DataNotFoundException("존재하지 않는 ID의 지하철역이 있습니다.");
        }
        return new Stations(stationGroup);
    }

    public void deleteStation(final Long id) {
        stationDao.deleteById(id);
    }
}
