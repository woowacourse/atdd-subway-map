package wooteco.subway.line.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.line.LineService;
import wooteco.subway.line.domain.Line;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/lines")
@RestController
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Long id = lineService.save(line);
        Line newLine = lineService.findLineById(id);
        return ResponseEntity.created(
                URI.create("/lines/" + newLine.getId()))
                .body(
                        new LineResponse(
                                newLine.getId(),
                                newLine.getName(),
                                newLine.getColor()));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineService.findAll();
        List<LineResponse> lineResponses = lines.stream()
                .map(line ->
                        new LineResponse(
                                line.getId(),
                                line.getName(),
                                line.getColor()))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        Line line = lineRequest.toEntity();
        lineService.update(id, line);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
