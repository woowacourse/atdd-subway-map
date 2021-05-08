package wooteco.subway.station;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.station.dao.StationDao;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class StationController {

    private final StationDao stationDao;

    public StationController(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(
            @RequestBody StationRequest stationRequest) {
        String name = stationRequest.getName();
        if (stationDao.findStationByName(name).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        Station station = Station.of(name);
        Station newStation = stationDao.save(station);
        StationResponse stationResponse = new StationResponse(newStation.getId(),
                newStation.getName());
        return ResponseEntity.created(URI.create("/stations/" + newStation.getId()))
                .body(stationResponse);
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        List<Station> stations = stationDao.findAll();
        List<StationResponse> stationResponses = stations.stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(stationResponses);
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationDao.remove(id);
        return ResponseEntity.noContent().build();
    }
}
