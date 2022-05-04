package wooteco.subway.ui;

import java.net.URI;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.dto.StationCreateRequest;
import wooteco.subway.dto.StationCreateResponse;
import wooteco.subway.service.StationService;

@RestController
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping("/stations")
    public ResponseEntity<StationCreateResponse> createStation(@RequestBody StationCreateRequest stationRequest) {
        final StationCreateResponse response = stationService.save(stationRequest);

        return ResponseEntity.created(URI.create("/stations/" + response.getId())).body(response);
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationCreateResponse>> showStations() {
        final List<StationCreateResponse> response = stationService.findAll();

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
