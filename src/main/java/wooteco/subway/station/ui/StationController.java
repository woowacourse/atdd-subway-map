package wooteco.subway.station.ui;

import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.repository.StationDao;
import wooteco.subway.station.service.StationService;
import wooteco.subway.station.ui.dto.StationRequest;
import wooteco.subway.station.ui.dto.StationResponse;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class StationController {

    private final StationService stationService;

    public StationController(final StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        Station newStation = stationService.createStation(station);
        StationResponse stationResponse = new StationResponse(newStation.getId(), newStation.getName());

        return ResponseEntity.created(
                URI.create("/stations/" + newStation.getId())
        ).body(stationResponse);
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        List<Station> stations = stationService.findAll();

        List<StationResponse> stationResponses = stations.stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(stationResponses);
    }

    @DeleteMapping("/stations/{id}")
    @SuppressWarnings({"rawtypes"})
    public ResponseEntity deleteStation(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<String> databaseExceptionHandle(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
