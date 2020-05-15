package wooteco.subway.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.ErrorResponse;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.exception.StartStationNotFoundException;
import wooteco.subway.admin.service.LineService;
import wooteco.subway.admin.service.StationService;

import java.net.URI;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class LineController {
    private final LineService lineService;
    private final StationService stationService;

    public LineController(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest view) {
        Line line = view.toLine();
        Line persistLine = lineService.save(line);

        return ResponseEntity
                .created(URI.create("/lines/" + persistLine.getId()))
                .body(LineResponse.of(persistLine));
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> getLine(@PathVariable Long id) {
        return ResponseEntity.ok()
                .body(findLineWithStationsById(id));
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineRequest view) {
        Line persistLine = lineService.updateLine(id, view.toLine());
        return ResponseEntity.ok()
                .body(LineResponse.of(persistLine));
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> getLines() {
        List<Line> lines = lineService.showLines();
        return ResponseEntity.ok()
                .body(findAllLineWithStations(lines));
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<LineResponse> deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent()
                .build();
    }

    @PutMapping("/lines/{id}/stations")
    public ResponseEntity<LineResponse> createLineStation(@PathVariable Long id,
                                                          @RequestBody LineStationCreateRequest lineStationCreateRequest) {
        lineService.addLineStation(id, lineStationCreateRequest.toLineStation());
        return ResponseEntity.ok()
                .body(findLineWithStationsById(id));
    }

    @DeleteMapping("/lines/{lineId}/stations/{stationId}")
    public void deleteLineStation(@PathVariable Long lineId, @PathVariable Long stationId) {
        lineService.removeLineStation(lineId, stationId);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponse> handleSQLException(SQLException e) {
        final ErrorResponse response = ErrorResponse.of("SQL 에러입니다.");
        return new ResponseEntity<>(response, HttpStatus.valueOf(500));
    }

    @ExceptionHandler(StartStationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStartStationException(StartStationNotFoundException e) {
        final ErrorResponse response = ErrorResponse.of("시작역을 찾을 수 없습니다.");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(StartStationNotFoundException e) {
        final ErrorResponse response = ErrorResponse.of(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private LineResponse findLineWithStationsById(Long id) {
        Line line = lineService.findById(id);
        List<Long> lineStationsId = line.getLineStationsId();
        Set<Station> stations = lineStationsId.stream()
                .map(stationService::findById)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return LineResponse.of(line, stations);
    }

    private List<LineResponse> findAllLineWithStations(List<Line> lines) {
        return lines.stream()
                .map(line -> findLineWithStationsById(line.getId()))
                .collect(Collectors.toList());
    }
}