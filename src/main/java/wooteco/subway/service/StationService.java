package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.domain.repository.StationRepository;
import wooteco.subway.domain.station.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {
    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public StationResponse createStation(StationRequest stationRequest) {
        Station station = stationRequest.createStation();
        long stationId = stationRepository.insertStation(station);
        return new StationResponse(stationId, station);
    }

    public List<StationResponse> findStations() {
        List<Station> stations = stationRepository.findStations();
        return stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    public void deleteStation(Long id) {
        stationRepository.deleteStation(id);
    }
}
