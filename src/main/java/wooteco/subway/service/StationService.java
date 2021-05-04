package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.controller.dto.request.StationRequest;
import wooteco.subway.controller.dto.response.StationResponse;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {
    public StationResponse createStation(StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        Station newStation = StationDao.save(station);
        return new StationResponse(newStation.getId(), newStation.getName());
    }

    public List<StationResponse> showStations() {
        List<Station> stations = StationDao.findAll();
        return stations.stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
    }

    public long deleteStation(Long id) {
        return StationDao.deleteById(id);
    }
}
