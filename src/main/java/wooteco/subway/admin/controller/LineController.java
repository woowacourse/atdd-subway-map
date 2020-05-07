package wooteco.subway.admin.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
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

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity create(@RequestBody LineRequest lineRequest) {
        Line savedLine = lineService.save(Line.of(lineRequest));
        final LineResponse line = LineResponse.of(savedLine);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(line);
    }

    @GetMapping
    public ResponseEntity readAll() {
        final List<LineResponse> lines = LineResponse.listOf(lineService.showLines());

        return ResponseEntity.ok(lines);
    }

    @GetMapping("/{id}")
    public ResponseEntity readById(@PathVariable Long id) {
        final LineResponse line = LineResponse.of(lineService.showLine(id));

        return ResponseEntity.ok(line);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity readByName(@PathVariable String name) {
        final LineResponse line = LineResponse.of(lineService.findByName(name));

        return ResponseEntity.ok(line);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateById(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        final Line line = lineService.showLine(id);
        line.update(lineRequest.toLine());
        lineService.updateLine(id, line);
        final LineResponse updatedLine = LineResponse.of(line);

        return ResponseEntity.ok(updatedLine);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteById(@PathVariable Long id) {
        lineService.deleteLineById(id);

        return ResponseEntity.ok().build();
    }
}
