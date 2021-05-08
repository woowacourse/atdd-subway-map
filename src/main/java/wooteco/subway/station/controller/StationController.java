package wooteco.subway.station.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.station.StationService;
import wooteco.subway.station.domain.Station;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/stations")
@RestController
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        Long id = stationService.save(station);
        Station newStation = stationService.findStationById(id);
        return ResponseEntity.created(
                URI.create("/stations/" + newStation.getId()))
                .body(
                        new StationResponse(
                                newStation.getId(),
                                newStation.getName()));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        List<Station> stations = stationService.findAll();
        List<StationResponse> stationResponses = stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(stationResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
