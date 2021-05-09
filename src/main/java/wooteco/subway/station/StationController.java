package wooteco.subway.station;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> create(
            @RequestBody StationRequest stationRequest) {
        String name = stationRequest.getName();
        Station createdStation = stationService.create(name);
        return ResponseEntity.created(URI.create("/stations/" + createdStation.getId()))
                .body(StationResponse.of(createdStation));
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showAll() {
        List<Station> stations = stationService.showAll();
        List<StationResponse> stationResponses = stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(stationResponses);
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        stationService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
