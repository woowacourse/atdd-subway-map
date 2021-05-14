package wooteco.subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.service.SubwayService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/stations")
public class StationController {
    private final SubwayService subwayService;

    public StationController(final SubwayService subwayService) {
        this.subwayService = subwayService;
    }

    @PostMapping
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        StationResponse stationResponse = subwayService.createStation(stationRequest);
        return ResponseEntity.created(URI.create("/stations/" + stationResponse.getId())).body(stationResponse);
    }

    @GetMapping
    public ResponseEntity<List<StationResponse>> findStations() {
        List<StationResponse> stationResponses = subwayService.findsStations();
        return ResponseEntity.ok().body(stationResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        subwayService.deleteStation(id);
        return ResponseEntity.noContent().build();
    }
}
