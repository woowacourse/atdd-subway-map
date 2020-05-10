package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

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

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@Valid @RequestBody LineRequest request) {
        LineResponse lineResponse = lineService.save(request);

        return ResponseEntity
            .created(URI.create("/lines/" + lineResponse.getId()))
            .body(lineResponse);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> getLines() {
        List<LineResponse> lines = lineService.findAll();

        return ResponseEntity
            .ok()
            .body(lines);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> getLine(@PathVariable Long id) {
        LineResponse lineResponse = lineService.findLineWithStationsById(id);

        return ResponseEntity
            .ok(lineResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateLine(@Valid @PathVariable Long id,
        @RequestBody LineRequest request) {
        lineService.updateLine(id, request);

        return ResponseEntity
            .ok()
            .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);

        return ResponseEntity
            .noContent()
            .build();
    }
}
