package wooteco.subway.ui;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.application.StationService;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.DuplicateNameException;
import wooteco.subway.exception.EmptyNameException;
import wooteco.subway.exception.NotExistStationException;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class StationController {

    private final StationService stationService = new StationService();

    @ExceptionHandler({DuplicateNameException.class, EmptyNameException.class})
    private ResponseEntity<Void> handleExceptionToBadRequest() {
        return ResponseEntity.badRequest().build();
    }
    
    @ExceptionHandler(NotExistStationException.class)
    private ResponseEntity<Void> handleExceptionToNotFound() {
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {

        Station station = stationService.saveByName(stationRequest.getName());

        StationResponse stationResponse = new StationResponse(station.getId(), station.getName());
        return ResponseEntity.created(URI.create("/stations/" + station.getId())).body(stationResponse);
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
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
