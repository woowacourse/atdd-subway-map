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
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.service.LineService;

@RestController
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
        Line line = lineRequest.toLine();

        if (lineService.existsByName(line.getName())) {
            return ResponseEntity
                .badRequest()
                .build();
        }

        Line persistLine = lineService.save(line);

        return ResponseEntity
            .created(URI.create("/lines/" + persistLine.getId()))
            .body(LineResponse.of(persistLine));
    }

    @GetMapping("/lines")
    public ResponseEntity showLines() {
        List<Line> persistLines = lineService.showLines();

        return ResponseEntity
            .ok()
            .body(persistLines);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity showLine(@PathVariable Long id) {
        Line persistLine = lineService.showLine(id);

        return ResponseEntity
            .ok()
            .body(persistLine);
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        Line line = lineService.showLine(id);
        line.update(lineRequest.toLine());
        lineService.updateLine(id, line);

        return ResponseEntity
            .ok()
            .body(LineResponse.of(line));
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }
}
