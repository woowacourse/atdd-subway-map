package wooteco.subway.presentation.station;

<<<<<<< HEAD
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
=======
>>>>>>> 3677b8f... refactor: custom exception으로 변경
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.application.station.StationService;
import wooteco.subway.domain.station.Station;
import wooteco.subway.domain.station.value.StationName;
import wooteco.subway.presentation.station.dto.StationDtoAssembler;
import wooteco.subway.presentation.station.dto.StationRequest;
import wooteco.subway.presentation.station.dto.StationResponse;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class StationController {

    private final StationDtoAssembler stationDtoAssembler;
    private final StationService stationService;

    public StationController(StationDtoAssembler stationDtoAssembler, StationService stationService) {
        this.stationDtoAssembler = stationDtoAssembler;
        this.stationService = stationService;
    }

    @PostMapping("/stations")
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        Station station = new Station(new StationName(stationRequest.getName()));
        Station newStation = stationService.createStation(station);
        StationResponse stationResponse = stationDtoAssembler.station(newStation);

        return ResponseEntity
                .created(URI.create("/stations/" + newStation.getId()))
                .body(stationResponse);
    }

    @GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StationResponse>> showStations() {
        List<Station> stations = stationService.findAll();

        List<StationResponse> stationResponses = stations.stream()
                .map(stationDtoAssembler::station)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(stationResponses);
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationService.delete(id);
        return ResponseEntity.noContent().build();
    }

<<<<<<< HEAD
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<String> duplicationKeyExceptionHandle(Exception e) {
        return ResponseEntity.badRequest().body("동일한 역을 등록할 수 없습니다");
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<String> databaseExceptionHandle(Exception e) {
        return ResponseEntity.badRequest().body("데이터베이스 에러입니다.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandle(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

=======
>>>>>>> 3677b8f... refactor: custom exception으로 변경
}
