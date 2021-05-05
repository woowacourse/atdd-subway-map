package wooteco.subway.station;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.exception.DuplicatedStationNameException;

@RestController
public class StationController {

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        StationDao stationDaoCache = new StationDaoCache();
        try {
            Station station = new Station(stationRequest.getName());
            Station newStation = stationDaoCache.save(station);
            StationResponse stationResponse = new StationResponse(newStation.getId(),
                newStation.getName());
            return ResponseEntity.created(URI.create("/stations/" + newStation.getId()))
                .body(stationResponse);
        } catch (DuplicatedStationNameException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        StationDaoCache stationDaoCache = new StationDaoCache();
        List<Station> stations = stationDaoCache.findAll();
        List<StationResponse> stationResponses = stations.stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(stationResponses);
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }
}
