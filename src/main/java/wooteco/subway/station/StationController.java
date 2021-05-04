package wooteco.subway.station;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class StationController {

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        StationResponse stationResponse = StationService.createStation(stationRequest.getName());
        return ResponseEntity.created(URI.create("/stations/" + stationResponse.getId())).body(stationResponse);
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        List<Station> stations = StationDao.findAll();
        List<StationResponse> stationResponses = stations.stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(stationResponses);
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<StationResponse> deleteStation(@PathVariable Long id) {
        StationResponse stationResponse = StationService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(stationResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handler(IllegalArgumentException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
