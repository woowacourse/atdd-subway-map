package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.service.LineService;

@RestController
@RequestMapping("/stations")
public class StationController {
    private final LineService lineService;

    public StationController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<Void> createStation(
        @RequestBody StationCreateRequest stationCreateRequest) {
        StationResponse persistStation = lineService.createStation(stationCreateRequest);

        return ResponseEntity
            .created(URI.create("/stations/" + persistStation.getId()))
            .build();
    }

    @GetMapping
    public ResponseEntity<List<StationResponse>> showStations() {
        return ResponseEntity
            .ok()
            .body(lineService.findAllStations());
    }

    @DeleteMapping("/{stationId}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long stationId) {
        lineService.deleteStationById(stationId);

        return ResponseEntity
            .noContent()
            .build();
    }
}
