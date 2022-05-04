package wooteco.subway.ui;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class StationController {

    private final StationDao stationDao;

    public StationController(StationDao stationDao){
        this.stationDao = stationDao;
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        String name = stationRequest.getName();
        Station station = stationDao.save(name);
        StationResponse stationResponse = new StationResponse(station);
        return ResponseEntity.created(URI.create("/stations/" + station.getId())).body(stationResponse);
    }

    // @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    // public ResponseEntity<List<StationResponse>> showStations() {
    //     List<Station> stations = stationDao.findAll();
    //     List<StationResponse> stationResponses = stations.stream()
    //             .map(it -> new StationResponse(it.getId(), it.getName()))
    //             .collect(Collectors.toList());
    //     return ResponseEntity.ok().body(stationResponses);
    // }
    //
    // @DeleteMapping("/stations/{id}")
    // public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
    //     return ResponseEntity.noContent().build();
    // }
}
