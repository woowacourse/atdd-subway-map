package wooteco.subway.line.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.line.controller.dto.LineRequest;
import wooteco.subway.line.controller.dto.LineResponse;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.service.LineService;

import java.net.URI;
import java.util.Collections;
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
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line newLine = lineService.save(line, lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
        LineResponse lineResponse = new LineResponse(newLine);
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line line = lineService.findById(id);
        return ResponseEntity.ok().body(new LineResponse(line));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineService.findAll();
        List<LineResponse> lineResponses = lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName().text(), line.getColor().text(), Collections.emptyList()))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        Line line = new Line(id, lineRequest.getName(), lineRequest.getColor());
        lineService.update(line);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        Line line = lineService.findById(id);
        lineService.delete(line);
        return ResponseEntity.noContent().build();
    }
}