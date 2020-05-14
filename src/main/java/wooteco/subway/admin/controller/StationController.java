package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.service.StationService;

@RestController
public class StationController {
    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping("/stations")
    public ResponseEntity<List<StationResponse>> showStations() {
        List<StationResponse> stations = stationService.getAll();
        return ResponseEntity.ok().body(stations);
    }

    @GetMapping("/stations/{id}")
    public ResponseEntity<StationResponse> getStation(@PathVariable Long id) {
        StationResponse stationResponse = stationService.getById(id);
        return ResponseEntity.ok(stationResponse);
    }

    @PostMapping("/stations")
    public ResponseEntity<Long> createStation(@RequestBody StationCreateRequest view) {
        Long savedId = stationService.save(view);

        return ResponseEntity
            .created(URI.create("/stations/" + savedId))
            .body(savedId);
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
