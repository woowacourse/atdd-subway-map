package wooteco.subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.controller.dto.ControllerDtoAssembler;
import wooteco.subway.controller.dto.station.StationRequest;
import wooteco.subway.controller.dto.station.StationResponse;
import wooteco.subway.service.StationService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stations")
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        StationResponse stationResponse = ControllerDtoAssembler.stationResponseByDto(stationService.createStation(stationRequest.getName()));
        return ResponseEntity.created(URI.create("/stations/" + stationResponse.getId())).body(stationResponse);
    }

    @GetMapping
    public ResponseEntity<List<StationResponse>> findStations() {
        List<StationResponse> stationResponses = stationService.findStations().stream()
                .map(ControllerDtoAssembler::stationResponseByDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(stationResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
