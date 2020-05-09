package wooteco.subway.admin.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.req.LineRequest;
import wooteco.subway.admin.dto.res.LineResponse;
import wooteco.subway.admin.service.LineService;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity create(@RequestBody LineRequest lineRequest) {
        LineResponse response = lineService.save(Line.of(lineRequest));
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    @GetMapping
    public ResponseEntity readAll() {
        final List<LineResponse> lines = lineService.showLines();

        return ResponseEntity.ok(lines);
    }

    @GetMapping("/{id}")
    public ResponseEntity readById(@PathVariable Long id) {
        final LineResponse line = lineService.showLine(id);

        return ResponseEntity.ok(line);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateById(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        LineResponse lineResponse = lineService.updateLine(id, lineRequest);

        return ResponseEntity.ok(lineResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteById(@PathVariable Long id) {
        lineService.deleteLineById(id);

        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity error(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
