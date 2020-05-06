package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.service.LineService;

import java.net.URI;

@RestController
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
        Line line = lineRequest.toLine();
        Line persistLine = lineService.save(line);

        return ResponseEntity
                .created(URI.create("/lines/" + persistLine.getId()))
                .body(LineResponse.of(line));
    }

    @GetMapping("/lines")
    public ResponseEntity showLines() {
        return ResponseEntity.ok().body(LineResponse.listOf(lineService.showLines()));
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity showLine(@PathVariable Long id) {
        LineResponse lineWithStationsById = lineService.findLineWithStationsById(id);
        return ResponseEntity.ok().body(lineWithStationsById);
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        Line line = lineRequest.toLine();
        lineService.updateLine(id, line);
        return ResponseEntity.ok().body(LineResponse.of(line));
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }
}
