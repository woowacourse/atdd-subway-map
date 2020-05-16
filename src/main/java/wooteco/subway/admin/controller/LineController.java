package wooteco.subway.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.service.LineService;

import javax.validation.Valid;
import java.util.List;

@RestControllerAdvice
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
    public ResponseEntity createLine(@RequestBody @Valid LineRequest view) {
        LineResponse persistLine = lineService.save(view);
        return new ResponseEntity(persistLine, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity showLine(@PathVariable Long id) {
        return new ResponseEntity(lineService.findById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody @Valid LineRequest view) {
        lineService.updateLine(id, view);
        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stations")
    public ResponseEntity<List<LineResponse>> findLinesWithStations() {
        List<LineResponse> lineResponses = lineService.findAllStationsWithLine();
        return new ResponseEntity<>(lineResponses, HttpStatus.OK);
    }

    @PostMapping("/{lineId}/stations")
    public ResponseEntity<Void> addStationByLineId(@PathVariable Long lineId, @RequestBody @Valid LineStationCreateRequest request) {
        lineService.addLineStation(lineId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @GetMapping("{lineId}/stations")
    public ResponseEntity<LineResponse> findStationsByLineId(@PathVariable Long lineId) {
        LineResponse lineResponse = lineService.findStationsByLineId(lineId);
        return ResponseEntity.ok(lineResponse);
    }

    @DeleteMapping("{lineId}/stations/{stationId}")
    public ResponseEntity deleteStationByLineId(@PathVariable Long lineId, @PathVariable Long stationId) {
        lineService.deleteStationByLineIdAndStationId(lineId, stationId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> illegalArgumentExceptionHandler(Exception e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> methodArgumentNotValidExceptionHandler(Exception e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}