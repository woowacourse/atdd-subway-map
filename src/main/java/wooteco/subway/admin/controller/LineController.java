package wooteco.subway.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.EdgeCreateRequest;
import wooteco.subway.admin.service.LineService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> getLines() {
        List<LineResponse> lineResponses = lineService.showLines();
        return new ResponseEntity<>(lineResponses, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> createLine(@RequestBody @Valid LineRequest view) {
        LineResponse persistLine = lineService.save(view);
        return ResponseEntity
                .created(URI.create(String.valueOf(persistLine.getId())))
                .build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        return new ResponseEntity<>(lineService.findById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody @Valid LineRequest view) {
        lineService.updateLine(id, view);
        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping("/stations")
    public ResponseEntity<List<LineResponse>> findLinesWithStations() {
        List<LineResponse> lineResponses = lineService.findAllStationsWithLine();
        return new ResponseEntity<>(lineResponses, HttpStatus.OK);
    }

    @PostMapping("/{lineId}/stations")
    public ResponseEntity<Void> addStationByLineId(@PathVariable Long lineId, @RequestBody @Valid EdgeCreateRequest request) {
        lineService.addEdge(lineId, request);
        return ResponseEntity
                .created(URI.create("/lines/" + lineId))
                .build();
    }

    @GetMapping("{lineId}/stations")
    public ResponseEntity<LineResponse> findStationsByLineId(@PathVariable Long lineId) {
        LineResponse lineResponse = lineService.findStationsByLineId(lineId);
        return ResponseEntity.ok(lineResponse);
    }

    @DeleteMapping("{lineId}/stations/{stationId}")
    public ResponseEntity<Void> deleteStationByLineId(@PathVariable Long lineId, @PathVariable Long stationId) {
        lineService.deleteStationByLineIdAndStationId(lineId, stationId);
        return ResponseEntity
                .noContent()
                .build();
    }
}