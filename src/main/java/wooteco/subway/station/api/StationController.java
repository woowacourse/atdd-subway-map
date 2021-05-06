package wooteco.subway.station.api;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import wooteco.subway.station.api.dto.StationRequest;
import wooteco.subway.station.api.dto.StationResponse;
import wooteco.subway.station.service.StationService;

@RestController
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(
            @RequestBody StationRequest stationRequest) {
        StationResponse newStation = stationService.createStation(stationRequest);
        return ResponseEntity.created(URI.create("/stations/" + newStation.getId()))
                .body(newStation);
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        List<StationResponse> stationResponses = stationService.findAll();
        return ResponseEntity.ok().body(stationResponses);
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        stationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
