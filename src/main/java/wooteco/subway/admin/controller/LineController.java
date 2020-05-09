package wooteco.subway.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.request.LineRequest;
import wooteco.subway.admin.dto.request.LineStationAddRequest;
import wooteco.subway.admin.dto.request.LineUpdateRequest;
import wooteco.subway.admin.dto.response.LineResponse;
import wooteco.subway.admin.dto.response.StationsAtLineResponse;
import wooteco.subway.admin.service.LineService;

import java.net.URI;
import java.util.List;

@RestController
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity createLine(@RequestBody LineRequest request) {
        Line line = request.toLine();
        LineResponse persistLine = lineService.save(line);
        return ResponseEntity
                .created(URI.create("/stations/" + persistLine.getId()))
                .body(persistLine);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity showLine(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(lineService.findLine(id));
    }

    @GetMapping("/lines")
    public ResponseEntity showLines() {
        return ResponseEntity.ok().body(lineService.showLines());
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity updateLine(@PathVariable("id") Long id, @RequestBody LineUpdateRequest request) {
        Line line = request.toLine();
        return ResponseEntity.ok().body(lineService.updateLine(id, line));
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable("id") Long id) {
        lineService.deleteLineById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @GetMapping("/lineStations")
    public ResponseEntity showLineStations() {
        List<StationsAtLineResponse> response = lineService.findAllLineStations();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/lineStations/{lineId}")
    public ResponseEntity createLineStations(@PathVariable("lineId") Long lineId, @RequestBody LineStationAddRequest request) {
        StationsAtLineResponse response = lineService.addLineStation(lineId, request);

        return ResponseEntity
                .created(URI.create("/lineStations/" + lineId))
                .body(response);
    }

    @DeleteMapping("/lineStations/{lineId}/{stationId}")
    public ResponseEntity<Void> deleteLineStation(@PathVariable("lineId") Long lineId, @PathVariable("stationId") Long stationId) {
        lineService.removeLineStation(lineId, stationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
