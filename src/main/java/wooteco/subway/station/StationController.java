package wooteco.subway.station;

import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;

@RestController
public class StationController {

    @Autowired
    private StationService service;

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StationResponse> exceptionHandler(IllegalArgumentException e) {
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(
        @RequestBody StationRequest stationRequest) {
        StationResponse response = service.create(stationRequest);
        return ResponseEntity.created(URI.create("/stations/" + response.getId()))
            .body(response);
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        return ResponseEntity.ok().body(service.findAll());
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
