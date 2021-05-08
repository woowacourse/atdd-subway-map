package wooteco.subway.station;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public List<StationResponse> findAll() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
            .map(it -> new StationResponse(it.getId(), it.getName()))
            .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        stationDao.deleteById(id);
    }

    public StationResponse create(StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        Station newStation = stationDao.save(station);
        return new StationResponse(newStation.getId(),
            newStation.getName());
    }
}
