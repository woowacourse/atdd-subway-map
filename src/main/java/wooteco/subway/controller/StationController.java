package wooteco.subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.service.dto.StationRequest;
import wooteco.subway.service.dto.StationResponse;

import java.net.URI;
import java.util.List;
import wooteco.subway.service.StationService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/stations")
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<StationResponse> createStation(@RequestBody @Valid StationRequest stationRequest) {
        long stationId = stationService.save(stationRequest);
        StationResponse stationResponse = new StationResponse(stationId, stationRequest.getName());
        return ResponseEntity.created(URI.create("/stations/" + stationId)).body(stationResponse);
    }

    @GetMapping
    public ResponseEntity<List<StationResponse>> showStations() {
        List<StationResponse> stationResponses = stationService.findAll();
        return ResponseEntity.ok().body(stationResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable @NotBlank Long id) {
        stationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
