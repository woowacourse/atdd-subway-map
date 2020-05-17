package wooteco.subway.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.service.LineService;
import wooteco.subway.admin.service.StationService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestControllerAdvice
@RequestMapping("/stations")
public class StationController {
    private final LineService lineService;
    private final StationService stationService;

    public StationController(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    @GetMapping
    public ResponseEntity<List<Station>> showStations() {
        return ResponseEntity.ok().body(lineService.showStations());
    }

    @PostMapping
    public ResponseEntity<Void> createStation(@RequestBody @Valid StationCreateRequest view) {
        Station station = new Station(view.getName());
        Station persistStation = lineService.save(station);
        return ResponseEntity
                .created(URI.create(String.valueOf(persistStation.getId())))
                .build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StationResponse> showStation(@PathVariable Long id) {
        return new ResponseEntity<>(stationService.findById(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        lineService.deleteStationById(id);
        return ResponseEntity
                .noContent()
                .build();
    }
}
