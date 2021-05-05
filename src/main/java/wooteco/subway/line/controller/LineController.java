package wooteco.subway.line.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.dto.LinesResponse;
import wooteco.subway.line.service.LineService;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@Transactional
public class LineController {
    private final LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = new Line(lineRequest.getColor(), lineRequest.getName());
        Line newLine = lineService.save(line);
        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LinesResponse>> getLines() {
        List<Line> lines = lineService.getLines();
        List<LinesResponse> linesResponses = new ArrayList<>();
        for (Line line : lines) {
            linesResponses.add(new LinesResponse(line.getId(), line.getName(), line.getColor()));
        }
        return ResponseEntity.ok().body(linesResponses);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> getLine(@PathVariable final Long id) {

        Line line = lineService.getLine(id);
        LineResponse lineResponse = new LineResponse(line.getId(), line.getName(), line.getColor());
        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable final Long id, @RequestBody LineRequest lineRequest) {
        lineService.updateLine(new Line(id, lineRequest.getColor(), lineRequest.getName()));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable final Long id) {
        lineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
