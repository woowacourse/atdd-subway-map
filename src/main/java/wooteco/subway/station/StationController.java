package wooteco.subway.station;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stations")
public class StationController {

    private final StationService stationService;

    public StationController(final StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<StationResponse> create(@RequestBody final StationRequest stationRequest) {
        final Station station = stationService.save(new Station(stationRequest.getName()));

        final StationResponse stationResponse = new StationResponse(station);
        return ResponseEntity.created(URI.create("/stations/" + station.getId())).body(stationResponse);
    }

    @GetMapping
    public ResponseEntity<List<StationResponse>> stations() {
        final List<Station> stations = stationService.findAll();

        final List<StationResponse> stationResponses = stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(stationResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final Long id) {
        stationService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
