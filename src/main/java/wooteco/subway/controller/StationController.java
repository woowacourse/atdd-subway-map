package wooteco.subway.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.service.StationService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/stations")
public class StationController {
    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        String name = stationRequest.getName();
        StationResponse stationResponse = stationService.createStation(name);
        URI location = URI.create("/stations/" + stationResponse.getId());
        return ResponseEntity
                .created(location)
                .body(stationResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        List<StationResponse> stationResponses = stationService.findAllStations();

        return ResponseEntity
                .ok()
                .body(stationResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        stationService.deleteStation(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
