package wooteco.subway.station;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.exception.service.ObjectNotFoundException;

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

    public Station findById(final Long id) {
        return stationDao.findById(id)
            .orElseThrow(() -> new ObjectNotFoundException("해당 Id의 지하철역이 없습니다."));
    }

    public void deleteStation(final Long id) {
        stationDao.deleteById(id);
    }
}
