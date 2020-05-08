package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.service.LineService;

import java.net.URI;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @GetMapping
    public ResponseEntity showLines() {
        return ResponseEntity.ok(lineService.showLines());
    }

    @PostMapping
    public ResponseEntity createLine(@RequestBody LineRequest view) {
        System.out.println(view.toString());
        Line persistLine = lineService.save(view.toLine());

        return ResponseEntity
                .created(URI.create("/lines" + persistLine.getId()))
                .body(LineResponse.of(persistLine));
    }

    @GetMapping("/{id}")
    public ResponseEntity showLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(LineResponse.of(lineService.findById(id)));
    }

    @PutMapping("/{id}")
    public void updateLine(@PathVariable Long id,@RequestBody LineRequest view) {
        lineService.updateLine(id, view.toLine());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }
}
