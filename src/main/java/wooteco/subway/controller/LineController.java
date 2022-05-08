package wooteco.subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.service.LineService;

import java.net.URI;
import java.util.List;

@RestController
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse lineResponse = lineService.create(lineRequest.getName(), lineRequest.getColor());
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResponses = lineService.findAll();
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("/lines/{lineId}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long lineId) {
        LineResponse lineResponse = lineService.findById(lineId);
        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping("/lines/{lineId}")
    public ResponseEntity<Void> updateLine(@PathVariable Long lineId, @RequestBody LineRequest lineRequest) {
        lineService.update(lineId, lineRequest.getName(), lineRequest.getColor());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{lineId}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long lineId) {
        lineService.delete(lineId);
        return ResponseEntity.noContent().build();
    }
}
