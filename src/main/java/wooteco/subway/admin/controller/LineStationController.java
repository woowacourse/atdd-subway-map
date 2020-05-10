package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.service.LineService;

@RestController
public class LineStationController {

    private LineService lineService;

    public LineStationController(LineService lineService) {
        this.lineService = lineService;
    }

    @GetMapping("/lineStations")
    public ResponseEntity showLinesWithStation() {
        return ResponseEntity.ok().body(lineService.showLinesWithStations());
    }

    @PostMapping("/lineStations/{lineId}")
    public ResponseEntity create(@PathVariable Long lineId,
            @RequestBody LineStationCreateRequest request) {
        lineService.addLineStation(lineId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/lineStations/{lineId}")
    public ResponseEntity showStationsByLineId(@PathVariable Long lineId) {
        return ResponseEntity.ok().body(lineService.findLineWithOrderedStationsById(lineId));
    }

    @DeleteMapping("/lines/{lineId}/lineStations/{stationId}")
    public ResponseEntity delete(@PathVariable Long lineId, @PathVariable Long stationId) {
        lineService.removeLineStation(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
