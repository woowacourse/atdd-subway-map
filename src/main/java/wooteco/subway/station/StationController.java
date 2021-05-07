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

    private final StationRepository stationRepository;

    public StationController(final StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @PostMapping
    public ResponseEntity<StationResponse> createStation(@RequestBody final StationRequest stationRequest) {
        final Station stationToSave = new Station(stationRequest.getName());
        final Station station = stationRepository.save(stationToSave);

        final StationResponse stationResponse = new StationResponse(station.getId(), station.getName());
        return ResponseEntity.created(URI.create("/stations/" + station.getId())).body(stationResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        final List<Station> stations = stationRepository.findAll();

        final List<StationResponse> stationResponses = stations.stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(stationResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteStation(@PathVariable final Long id) {
        final Station station = stationRepository.findById(id);
        stationRepository.delete(station);

        return ResponseEntity.noContent().build();
    }
}
