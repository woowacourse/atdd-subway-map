package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.service.LineService;

@RestController
@RequestMapping("/api/stations")
public class StationController {
    private final LineService service;

    public StationController(LineService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<StationResponse> createStation(
        @RequestBody StationCreateRequest request) {
        Station station = request.toStation();
        Station persistStation = service.saveStation(station);

        return ResponseEntity
            .created(URI.create("/api/stations/" + persistStation.getId()))
            .body(StationResponse.of(persistStation));
    }

    @GetMapping
    public ResponseEntity<List<StationResponse>> showStations() {
        final List<Station> stations = service.findAllStations();
        return ResponseEntity.ok().body(StationResponse.listOf(stations));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        service.deleteStationById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{name}")
    public ResponseEntity<Long> getStationidByName(@PathVariable String name) {
        return ResponseEntity
            .ok()
            .body(service.findStationIdByName(name));
    }
}
