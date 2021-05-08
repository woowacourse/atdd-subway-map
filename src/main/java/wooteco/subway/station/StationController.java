package wooteco.subway.station;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stations")
public final class StationController {

    private final StationService stationService;

    public StationController(final StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<StationResponse> createStation(@RequestBody final StationRequest stationRequest) {
        final Station requestedStation = new Station(stationRequest);

        final StationDto createdLineInfo = stationService.save(requestedStation);
        final Long stationId = createdLineInfo.getId();
        final String stationName = createdLineInfo.getName();

        final StationResponse stationResponse = new StationResponse(stationId, stationName);
        return ResponseEntity.created(URI.create("/stations/" + stationId)).body(stationResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        final List<StationDto> stationsInfo = stationService.showAll();

        final List<StationResponse> stationResponses = stationsInfo.stream()
                .map(info -> new StationResponse(info.getId(), info.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(stationResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable final Long id) {
        stationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
