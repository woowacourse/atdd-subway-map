package wooteco.subway.ui;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.service.StationService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class StationController {

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        StationResponse stationResponse = StationService.createStation(stationRequest);
        return ResponseEntity.created(URI.create("/stations/" + stationResponse.getId())).body(stationResponse);
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        List<StationResponse> stationResponses = StationService.findAll();
        return ResponseEntity.ok().body(stationResponses);
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        StationService.deleteStation(id);
        return ResponseEntity.ok().build();
    }
}
