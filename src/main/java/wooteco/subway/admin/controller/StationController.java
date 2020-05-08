package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.service.LineService;

import java.net.URI;

@RestController
@RequestMapping("/stations")
public class StationController {
    private final LineService lineService;

    public StationController(LineService lineService) {
        this.lineService = lineService;
    }

    @GetMapping
    public ResponseEntity showStations() {
        return ResponseEntity.ok().body(lineService.showStations());
    }

    @PostMapping
    public ResponseEntity createStation(@RequestBody StationCreateRequest view) {
        Station persistStation = lineService.save(view.toStation());
        return ResponseEntity
                .created(URI.create("/stations/" + persistStation.getId()))
                .body(StationResponse.of(persistStation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        lineService.deleteStationById(id);
        return ResponseEntity.noContent().build();
    }
}
