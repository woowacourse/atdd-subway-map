package wooteco.subway.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.controller.exception.DuplicateStationNameException;
import wooteco.subway.dao.StationRepository;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stations")
public class StationController {
    private final StationRepository stationRepository;

    public StationController(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @PostMapping
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        String name = stationRequest.getName();
        Station station = new Station(name);
        validateDuplicateStationName(name);

        Station newStation = stationRepository.save(station);
        StationResponse stationResponse = new StationResponse(newStation.getId(), newStation.getName());

        URI location = URI.create("/stations/" + stationResponse.getId());
        return ResponseEntity.created(location).build();
    }

    private void validateDuplicateStationName(String name) {
        this.stationRepository.findByName(name).ifPresent(line -> {
            throw new DuplicateStationNameException(name);
        });
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        List<StationResponse> stationResponses = stationRepository.findAll().stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(stationResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        stationRepository.delete(id);
        return ResponseEntity.noContent()
                .build();
    }
}
