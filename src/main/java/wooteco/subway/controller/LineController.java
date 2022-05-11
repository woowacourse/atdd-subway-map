package wooteco.subway.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.repository.exception.DuplicateLineColorException;
import wooteco.subway.repository.exception.DuplicateLineNameException;
import wooteco.subway.service.LineService;
import wooteco.subway.service.dto.line.LineRequest;
import wooteco.subway.service.dto.line.LineResponse;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse lineResponse = lineService.create(lineRequest.getName(), lineRequest.getColor());
        URI redirectUri = URI.create("/lines/" + lineResponse.getId());
        return ResponseEntity.created(redirectUri).body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResponses = lineService.findAll();
        return ResponseEntity.ok(lineResponses);
    }

    @GetMapping("/{lineId}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long lineId) {
        LineResponse lineResponse = lineService.findOne(lineId);
        return ResponseEntity.ok(lineResponse);
    }

    @PutMapping("/{lineId}")
    public ResponseEntity<Void> updateLine(@PathVariable Long lineId, @RequestBody LineRequest lineRequest) {
        lineService.update(lineId, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineId}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long lineId) {
        lineService.remove(lineId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler({DuplicateLineNameException.class, DuplicateLineColorException.class})
    public ResponseEntity<String> handle(RuntimeException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }
}
