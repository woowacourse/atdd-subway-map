package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.service.StationService;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/stations")
public class StationController {

    private final StationService stationService;

    public StationController(final StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping
    public ResponseEntity<?> showStations() {
        return ResponseEntity.ok().body(stationService.findAllStations());
    }

    @PostMapping
    public ResponseEntity<?> createStation(
            @Valid @RequestBody StationCreateRequest stationRequest) {
        Station persistStation = stationService.create(stationRequest.toStation());

        return ResponseEntity
                .created(URI.create("/stations/" + persistStation.getId()))
                .body("{}");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStation(@PathVariable Long id) {
        stationService.deleteStationById(id);
        return ResponseEntity.noContent().build();
    }
}
