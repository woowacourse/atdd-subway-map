package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.LineWithStationsResponse;
import wooteco.subway.admin.service.LineService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/line-stations")
public class LineStationController {

    private LineService lineService;

    public LineStationController(LineService lineService) {
        this.lineService = lineService;
    }

    @GetMapping
    public ResponseEntity<List<LineWithStationsResponse>> showLinesWithStation() {
        return ResponseEntity.ok().body(lineService.showLinesWithStations());
    }

    @PostMapping("/{lineId}")
    public ResponseEntity<LineResponse> create(@PathVariable Long lineId, @RequestBody LineStationCreateRequest request) {
        Line persistLine = lineService.addLineStation(lineId, request.toLineStation());

        return ResponseEntity
                .created(URI.create("/lineStations/" + persistLine.getId()))
                .body(LineResponse.of(persistLine));
    }

    @GetMapping("/{lineId}")
    public ResponseEntity<LineWithStationsResponse> showStationsByLineId(@PathVariable Long lineId) {
        return ResponseEntity.ok().body(lineService.findLineWithStationsById(lineId));
    }

    @DeleteMapping("/{lineId}/{stationId}")
    public ResponseEntity delete(@PathVariable Long lineId, @PathVariable Long stationId) {
        lineService.removeLineStation(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
