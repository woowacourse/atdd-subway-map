package wooteco.subway.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.service.LineService;

import java.net.URI;
import java.util.List;

@RestController
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity createLines(@RequestBody LineRequest request) {
        Line line = lineService.save(request.toLine());

        return ResponseEntity
                .created(URI.create("/lines/" + line.getId()))
                .body(LineResponse.of(line));
    }

    @GetMapping("/lines")
    public List<LineResponse> getLines() {
        return LineResponse.listOf(lineService.showLines());
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity updateLine(@PathVariable("id") Long id, @RequestBody LineRequest request) {
        lineService.updateLine(id, request.toLine());

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/lines/{id}")
    public LineResponse getLine(@PathVariable("id") Long id) {
        return lineService.findLineWithStationsById(id);
    }

    @DeleteMapping("/lines/{id}")
    public void deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
    }
}
