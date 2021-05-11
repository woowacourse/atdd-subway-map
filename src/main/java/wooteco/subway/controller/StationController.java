package wooteco.subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.domain.station.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.service.SubwayService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stations")
public class StationController {
    private final SubwayService subwayService;

    public StationController(final SubwayService subwayService) {
        this.subwayService = subwayService;
    }

    @PostMapping
    public ResponseEntity<StationResponse> createStation(
            @RequestBody StationRequest stationRequest) {
        Station station = stationRequest.createStation();
        long id = subwayService.createStation(station);
        return ResponseEntity.created(URI.create("/stations/" + id)).body(new StationResponse(id, station));
    }

    @GetMapping
    public ResponseEntity<List<StationResponse>> showStations() {
        List<Station> stations = subwayService.showStations();
        List<StationResponse> stationResponses = stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(stationResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteStation(@PathVariable long id) {
        subwayService.deleteStation(id);
        return ResponseEntity.noContent().build();
    }
}
