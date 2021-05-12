package wooteco.subway.line.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.dto.LineInfo;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.dto.SectionRequest;
import wooteco.subway.line.service.LineService;

import javax.validation.Valid;
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
    public ResponseEntity<LineResponse> save(@Validated(LineInfo.save.class) @RequestBody LineRequest lineRequest) {
        Line line = lineService.save(lineRequest);
        LineResponse response = new LineResponse(line);
        return ResponseEntity.created(URI.create("/lines/" + response.getId())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> getLines() {
        List<Line> line = lineService.findAll();
        List<LineResponse> lineResponses = line.stream()
                .map(it -> new LineResponse(it.id(), it.name(), it.color(), Collections.emptyList()))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("/{id:[\\d]+}")
    public ResponseEntity<LineResponse> getLine(@PathVariable Long id) {
        Line findLine = lineService.findById(id);

        LineResponse response = new LineResponse(findLine.id(), findLine.name(), findLine.color(), findLine.stations());
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{id:[\\d]+}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id,
                                           @Valid @RequestBody LineRequest lineRequest) {
        lineService.update(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id:[\\d]+}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id:[\\d]+}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable("id") Long lineId, @RequestParam Long stationId) {
        System.out.println(stationId);
        lineService.deleteSection(lineId, stationId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id:[\\d]+}/sections")
    public ResponseEntity addSection(@PathVariable Long id, @Valid @RequestBody SectionRequest sectionRequest) {
        lineService.addSection(id, sectionRequest);
        return ResponseEntity.ok().build();
    }
}
