package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.service.StationService;

import java.net.URI;

@RestController
public class StationController {
    private final StationService stationService;

    public StationController(final StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping("/stations")
    public ResponseEntity createStation(@RequestBody StationRequest stationRequest) {
        Station persistStation = stationService.save(stationRequest.getName());
        return ResponseEntity
            .created(URI.create("/stations/" + persistStation.getId()))
            .body(StationResponse.of(persistStation));
    }

    @GetMapping("/stations")
    public ResponseEntity showStations() {
        return ResponseEntity.ok().body(stationService.findAll());
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        stationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler
    public ResponseEntity exceptionHandler(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
