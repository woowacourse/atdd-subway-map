package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.service.StationService;

import java.net.URI;

@RestController
public class StationController {
    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationCreateRequest view) {
        Station station = stationService.save(view.getName());
        return ResponseEntity
                .created(URI.create("/stations/" + station.getId()))
                .body(StationResponse.of(station));
    }

    @GetMapping("/stations")
    public ResponseEntity<Iterable<Station>> showStations() {
        Iterable<Station> stations = stationService.findAllOfStations();
        return ResponseEntity.ok()
                .body(stations);
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationService.deleteStationById(id);
        return ResponseEntity.noContent()
                .build();
    }
}
