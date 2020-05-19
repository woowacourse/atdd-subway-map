package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.request.StationCreateRequest;
import wooteco.subway.admin.dto.response.StationResponse;
import wooteco.subway.admin.service.StationService;

import java.net.URI;

@RestController
@RequestMapping("/stations")
public class StationController {
    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping()
    public ResponseEntity<StationResponse> createStation(@RequestBody StationCreateRequest request) {
        StationResponse response = stationService.save(request);

        return ResponseEntity
                .created(URI.create("/stations/" + response.getId()))
                .body(response);
    }

    @GetMapping()
    public ResponseEntity<Iterable<Station>> showStations() {
        Iterable<Station> response = stationService.findAll();

        return ResponseEntity.ok()
                .body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable("id") Long id) {
        stationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
