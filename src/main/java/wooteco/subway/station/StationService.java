package wooteco.subway.station;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.DataNotFoundException;

@Transactional
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

    @Transactional(readOnly = true)
    public List<StationResponse> findStations() {
        final List<Station> stations = stationDao.findAll();
        return stations.stream()
            .map(station -> new StationResponse(station.getId(), station.getName()))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Station findById(final Long id) {
        return stationDao.findById(id)
            .orElseThrow(() -> new DataNotFoundException("해당 Id의 지하철역이 없습니다."));
    }

    public void deleteStation(final Long id) {
        stationDao.deleteById(id);
    }
}
