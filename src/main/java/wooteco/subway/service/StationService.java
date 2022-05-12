package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.domain.Station;
import wooteco.subway.domain.StationSeries;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.repository.StationRepository;

@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public StationResponse save(StationRequest stationRequest) {
        StationSeries stationSeries = new StationSeries(stationRepository.findAllStations());
        Station createdStation = stationRepository.create(stationSeries.create(stationRequest.getName()));
        return StationResponse.from(createdStation);
    }

    public List<StationResponse> findAll() {
        return stationRepository.findAllStations()
            .stream()
            .map(StationResponse::from)
            .collect(Collectors.toList());
    }

    public void deleteOne(Long id) {
        stationRepository.deleteById(id);
    }
}
