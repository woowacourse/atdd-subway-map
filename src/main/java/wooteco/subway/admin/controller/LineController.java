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
    public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
        Line saved = lineService.save(lineRequest.toLine());
        return ResponseEntity
            .created(URI.create("/lines/" + saved.getId()))
            .build();
    }

    @GetMapping
    public ResponseEntity getLines() {
        List<Line> lines = lineService.showLines();
        return ResponseEntity.ok(LineResponse.listOf(lines));
    }

    @GetMapping("/{id}")
    public ResponseEntity getLine(@PathVariable("id") Long id) {
        return ResponseEntity.ok(lineService.findLineWithStationsById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity updateLine(@PathVariable("id") Long id, @RequestBody LineRequest lineRequest) {
        lineService.updateLine(id, lineRequest.toLine());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable("id") Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }
}
