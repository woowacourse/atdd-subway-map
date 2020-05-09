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
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> getLines() {
        List<LineResponse> lineResponses = lineService.showLines();
        return new ResponseEntity<>(lineResponses, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity createLine(@RequestBody LineRequest view) {
        LineResponse persistLine = lineService.save(view.toLine());
        return new ResponseEntity(persistLine, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity showLine(@PathVariable Long id) {
        return new ResponseEntity(lineService.findById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest view) {
        LineResponse line = lineService.updateLine(id, view.toLine());
        return new ResponseEntity(line, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }
}
