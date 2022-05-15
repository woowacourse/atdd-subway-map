package wooteco.subway.ui;

import java.net.URI;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.service.StationService;
import wooteco.subway.service.dto.StationServiceResponse;
import wooteco.subway.ui.dto.StationRequest;

@RestController
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping("/stations")
    public ResponseEntity<StationServiceResponse> createStation(
        @Validated @RequestBody StationRequest stationRequest) {
        StationServiceResponse stationServiceResponse = stationService.save(
            stationRequest.toServiceRequest());
        return ResponseEntity.created(URI.create("/stations/" + stationServiceResponse.getId()))
            .body(
                stationServiceResponse);
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationServiceResponse>> showStations() {
        List<StationServiceResponse> stations = stationService.findAll();
        return ResponseEntity.ok().body(stations);
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        if (stationService.deleteById(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.noContent().build();
    }
}
