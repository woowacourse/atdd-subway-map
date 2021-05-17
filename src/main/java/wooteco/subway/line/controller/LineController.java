package wooteco.subway.line.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.dto.LineWithStationsResponse;
import wooteco.subway.line.service.LineService;
import wooteco.subway.station.domain.Station;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineWithStationsResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line newLine = lineService.add(lineRequest);
        List<Station> stationsByLine = lineService.findStationsByLineId(newLine.getId());

        LineWithStationsResponse lineWithStationsResponse = new LineWithStationsResponse(newLine, stationsByLine);
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineWithStationsResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineService.findAll();
        List<LineResponse> lineResponses = lines.stream()
            .map(LineResponse::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(lineResponses);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineWithStationsResponse> showLine(@PathVariable Long id) {
        Line line = lineService.findById(id);
        List<Station> stationsByLine = lineService.findStationsByLineId(line.getId());

        LineWithStationsResponse lineWithStationsResponse = new LineWithStationsResponse(line, stationsByLine);
        return ResponseEntity.ok(lineWithStationsResponse);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.update(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
