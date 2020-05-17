package wooteco.subway.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.service.LineService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<LineResponse> createLine(@RequestBody @Valid LineRequest view) {
        Line line = lineService.save(view.toLine());
        return new ResponseEntity(LineResponse.of(line), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        return new ResponseEntity(lineService.findById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody @Valid LineRequest view) {
        LineResponse line = lineService.updateLine(id, view.toLine());
        return new ResponseEntity(line, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stations")
    public ResponseEntity<List<LineResponse>> findLinesWithStations() {
        List<Line> lines = lineService.findAll();
        List<Long> allLineId = lineService.findAllLineId(lines);
        List<LineResponse> lineResponses = lines.stream()
                .map(line -> LineResponse.of(line, lineService.findAllById(allLineId)))
                .collect(Collectors.toList());
        return new ResponseEntity<>(lineResponses, HttpStatus.OK);
    }

    @PostMapping("/{lineId}/stations")
    public ResponseEntity<Void> addStationByLineId(@PathVariable Long lineId, @RequestBody LineStationCreateRequest request) {
        lineService.addLineStation(lineId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @GetMapping("{lineId}/stations")
    public ResponseEntity<LineResponse> findStationsByLineId(@PathVariable Long lineId) {
        Line line = lineService.findLineById(lineId);
        LineResponse lineResponse = LineResponse.of(line, lineService.findAllById(line.getLineStationsId()));
        return ResponseEntity.ok(lineResponse);
    }

    @DeleteMapping("{lineId}/stations/{stationId}")
    public ResponseEntity<Void> deleteStationByLineId(@PathVariable Long lineId, @PathVariable Long stationId) {
        lineService.deleteStationByLineIdAndStationId(lineId, stationId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}