package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.service.StationService;
import wooteco.subway.service.dto.StationRequest;
import wooteco.subway.service.dto.StationResponse;

import java.net.URI;
import java.util.List;

@RestController
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        StationResponse stationResponse = stationService.save(stationRequest);
        return ResponseEntity.created(URI.create("/stations/" + stationResponse.getId())).body(stationResponse);
    }

    @GetMapping("/stations")
    public ResponseEntity<List<StationResponse>> showStations() {
        return ResponseEntity.ok().body(stationService.showStations());
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationService.deleteStation(id);
        return ResponseEntity.noContent().build();
    }
}
