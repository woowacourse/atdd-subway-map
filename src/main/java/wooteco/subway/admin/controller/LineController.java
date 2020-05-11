package wooteco.subway.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.service.LineService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        try {
            Line line = lineRequest.toLine();
            Line persistLine = lineService.save(line);
            return ResponseEntity
                    .created(URI.create("/lines/" + persistLine.getId()))
                    .body(LineResponse.from(line));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(null);
        }
    }

    @GetMapping("")
    public ResponseEntity showLines() {
        List<LineResponse> allLinesWithStations = lineService.findAllLinesWithStations();
        return ResponseEntity.ok().body(allLinesWithStations);
    }

    @GetMapping("/{id}")
    public ResponseEntity showLine(@PathVariable Long id) {
        try {
            LineResponse lineResponse = lineService.findStationsByLineId(id);
            return ResponseEntity.ok().body(lineResponse);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/stations")
    public ResponseEntity showStationsOfLine(@PathVariable Long id) {
        LineResponse lineResponse = lineService.findStationsByLineId(id);
        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        try {
            Line line = lineRequest.toLine();
            lineService.updateLine(id, line);
            return ResponseEntity.ok().body(LineResponse.from(line));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/line-stations")
    public ResponseEntity registerStation(@PathVariable Long id,
                                          @RequestBody LineStationCreateRequest lineStationCreateRequest) {
        try {
            Line line = lineService.addLineStation(id, lineStationCreateRequest);
            return ResponseEntity
                    .created(URI.create("/lines/" + id))
                    .body(LineResponse.from(line));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @DeleteMapping("/{lineId}/line-stations/{stationId}")
    public ResponseEntity deleteLineStation(@PathVariable Long lineId, @PathVariable Long stationId) {
        lineService.removeLineStation(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
