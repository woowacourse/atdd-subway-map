package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.service.LineService;

import java.net.URI;

@RestController
@RequestMapping("/line")
public class LineController {
    private LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity create(@RequestBody LineRequest lineRequest) {
        Line persistLine = lineService.save(lineRequest.toLine());

        return ResponseEntity
                .created(URI.create("/line/" + persistLine.getId()))
                .body(LineResponse.of(persistLine));
    }

    @GetMapping
    public ResponseEntity showLines() {
        return ResponseEntity.ok().body(lineService.showLines());
    }

    @GetMapping("/{lineId}")
    public ResponseEntity showLineById(@PathVariable Long lineId) {
        return ResponseEntity.ok().body(lineService.findLineById(lineId));
    }

    @PutMapping("/{lineId}")
    public ResponseEntity update(@PathVariable Long lineId, @RequestBody LineRequest lineRequest) {
        lineService.updateLine(lineId, lineRequest.toLine());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineId}")
    public ResponseEntity delete(@PathVariable Long lineId) {
        lineService.deleteLineById(lineId);
        return ResponseEntity.ok().build();
    }
}
