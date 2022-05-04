package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDaoImpl;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@Service
public class StationService {

    private final StationDaoImpl stationDaoImpl;

    public StationService(StationDaoImpl stationDaoImpl) {
        this.stationDaoImpl = stationDaoImpl;
    }

    public StationResponse create(StationRequest stationRequest) {
        String name = stationRequest.getName();
        Station station = stationDaoImpl.save(name);
        return new StationResponse(station);
    }

    public List<StationResponse> show() {
        List<Station> stations = stationDaoImpl.findAll();
        return stations.stream()
            .map(StationResponse::new)
            .collect(Collectors.toList());
    }

    public void delete(Long id) {
        stationDaoImpl.deleteById(id);
    }
}
