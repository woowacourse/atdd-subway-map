package wooteco.subway.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.service.LineService;

import java.util.List;

@RestController
@RequestMapping("/stations")
public class StationController {
    private final LineService lineService;

    public StationController(LineService lineService) {
        this.lineService = lineService;
    }

    @GetMapping
    public ResponseEntity<List<Station>> showStations() {
        return ResponseEntity.ok().body(lineService.showStations());
    }

    @PostMapping
    public ResponseEntity<StationResponse> createStation(@RequestBody StationCreateRequest view) {
        Station persistStation = lineService.save(view.toStation());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(StationResponse.of(persistStation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        lineService.deleteStationById(id);
        return ResponseEntity.noContent().build();
    }
}
