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
    ResponseEntity<Void> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse persistLine = lineService.addLine(lineRequest.toLine());

        return ResponseEntity
            .created(URI.create("/lines/" + persistLine.getId()))
            .build();
    }

    @GetMapping
    ResponseEntity<List<LineResponse>> getLines() {
        List<LineResponse> lines = lineService.showLines();

        return ResponseEntity
            .ok(lines);
    }

    @GetMapping("/{id}")
    ResponseEntity<LineResponse> getLine(@PathVariable Long id) {
        LineResponse lineResponse = lineService.findLineById(id);

        return ResponseEntity
            .ok(lineResponse);
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> updateLine(@PathVariable Long id,
        @RequestBody LineRequest lineRequest) {
        lineService.updateLine(id, lineRequest.toLine());

        return ResponseEntity
            .noContent()
            .build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);

        return ResponseEntity
            .noContent()
            .build();
    }
}
