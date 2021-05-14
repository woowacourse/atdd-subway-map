package wooteco.subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.dao.LineRepository;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.service.LineService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;
    private final LineRepository lineRepository;

    public LineController(LineService lineService, LineRepository lineRepository) {
        this.lineService = lineService;
        this.lineRepository = lineRepository;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLines(@RequestBody LineRequest lineRequest) {
        LineResponse lineResponse = this.lineService.createLine(lineRequest);

        URI location = URI.create("/lines/" + lineResponse.getId());
        return ResponseEntity
                .created(location)
                .body(lineResponse);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResponses = this.lineService.findAllLines();

        return ResponseEntity
                .ok()
                .body(lineResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> getLine(@PathVariable long id) {
        LineResponse lineResponse = this.lineService.findLineById(id);

        return ResponseEntity
                .ok()
                .body(lineResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable long id, @RequestBody LineRequest lineRequest) {
        this.lineService.updateLine(id, lineRequest.getName(), lineRequest.getColor());

        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<LineResponse> deleteLine(@PathVariable long id) {
        lineService.deleteLine(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
