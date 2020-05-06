package wooteco.subway.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineCreateRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineUpdateRequest;
import wooteco.subway.admin.service.LineService;

import java.util.List;

@RestController
@RequestMapping("lines")
public class LineController {

    private final LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<Line> createLine(@RequestBody LineCreateRequest lineCreateRequest) {
        return new ResponseEntity<>(lineService.save(lineCreateRequest.toLine()), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> getLines() {
        return ResponseEntity.ok(lineService.getLineResponses());
    }

    @GetMapping("{id}")
    public ResponseEntity<LineResponse> getLine(@PathVariable("id") Long lineId) {
        return ResponseEntity.ok(lineService.findLineWithStationsById(lineId));
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> updateLine(@PathVariable("id") Long lineId,
                                           @RequestBody LineUpdateRequest lineUpdateRequest) {
        lineService.updateLine(lineId, lineUpdateRequest.toLine());
        return ResponseEntity.ok().build();
    }
}
