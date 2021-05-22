package wooteco.subway.station;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse save(StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        Station newStation = stationDao.save(station);
        return new StationResponse(newStation);
    }

    public List<StationResponse> findAll() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
            .map(StationResponse::new)
            .collect(Collectors.toList());
    }

    public void deleteById(Long stationId) {
        stationDao.delete(stationId);
    }

    public List<Station> findByLineId(Long lineId) {
        return stationDao.findByLineId(lineId);
    }
}
