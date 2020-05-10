package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.service.LineService;

@RequestMapping("/lines")
@RestController
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line persistLine = lineService.save(lineRequest.toLine());

        return ResponseEntity
                .created(URI.create("/lines/" + persistLine.getId()))
                .body(LineResponse.of(persistLine));
    }

    @GetMapping
    ResponseEntity<List<LineResponse>> getLines() {
        List<Line> lines = lineService.showLines();

        return ResponseEntity
                .ok(LineResponse.listOf(lines));
    }

    @GetMapping("/{id}")
    ResponseEntity<LineResponse> getLine(@PathVariable Long id) {
        LineResponse lineResponse = lineService.findLineById(id);

        return ResponseEntity
                .ok(lineResponse);
    }

    @PutMapping("/{id}")
    ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        Line line = lineService.updateLine(id, lineRequest.toLine());

        return ResponseEntity
                .ok(LineResponse.of(line));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
