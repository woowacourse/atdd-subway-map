package wooteco.subway.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.exception.StationNotFoundException;
import wooteco.subway.admin.repository.StationRepository;

@Service
public class StationService {
    private StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Station createStation(StationCreateRequest stationCreateRequest) {
        Station station = stationCreateRequest.toStation();
        return stationRepository.save(station);
    }

    public List<StationResponse> findAllStations() {
        List<Station> persistStations = stationRepository.findAll();
        return StationResponse.listOf(persistStations);
    }

    public void deleteStationById(Long stationId) {
        stationRepository.deleteById(stationId);
    }

    public StationResponse findById(Long stationId) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(StationNotFoundException::new);
        return StationResponse.of(station);
    }
}
