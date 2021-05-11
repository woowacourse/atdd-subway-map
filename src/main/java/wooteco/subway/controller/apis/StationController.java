package wooteco.subway.controller.apis;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.controller.dto.StationRequest;
import wooteco.subway.controller.dto.StationResponse;
import wooteco.subway.domain.station.Station;
import wooteco.subway.service.StationService;

import javax.validation.Valid;
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
    public ResponseEntity<StationResponse> createStation(@RequestBody @Valid StationRequest stationRequest) {
        Station savedStation = stationService.createStation(stationRequest.getName());
        StationResponse stationResponse = StationResponse.from(savedStation);
        URI uri = URI.create("/stations/" + savedStation.getId());
        return ResponseEntity.created(uri)
                .body(stationResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        List<Station> stations = stationService.findAll();
        List<StationResponse> stationResponses = StationResponse.fromList(stations);
        return ResponseEntity.ok(stationResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationService.deleteById(id);
        return ResponseEntity.noContent()
                .build();
    }
}
