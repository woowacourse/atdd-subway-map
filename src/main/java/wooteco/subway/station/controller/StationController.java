package wooteco.subway.station.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;
import wooteco.subway.station.service.StationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@Validated
@RestController
@RequestMapping("/stations")
public class StationController {

    private final StationService stationService;

    public StationController(final StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<StationResponse> create(@RequestBody @Valid final StationRequest stationRequest) {
        final StationResponse station = stationService.save(stationRequest);
        final URI responseUri = URI.create("/stations/" + station.getId());

        return ResponseEntity.created(responseUri).body(station);
    }

    @GetMapping
    public ResponseEntity<List<StationResponse>> stations() {
        return ResponseEntity.ok(stationService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive final Long id) {
        stationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
