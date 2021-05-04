package wooteco.subway.station;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class StationController {

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        String stationName = stationRequest.getName();
        Optional<Station> duplicateStation = StationDao.findByName(stationName);
        if (duplicateStation.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        Station station = new Station(stationName);
        Station newStation = StationDao.save(station);
        StationResponse stationResponse = new StationResponse(newStation.getId(), newStation.getName());
        return ResponseEntity.created(URI.create("/stations/" + newStation.getId())).body(stationResponse);
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
    public ResponseEntity<String> deleteStation(@PathVariable Long id) {
        Optional<Station> station = StationDao.findById(id);
        if(!station.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        StationDao.delete(station.get());
        return ResponseEntity.noContent().build();
    }
}
