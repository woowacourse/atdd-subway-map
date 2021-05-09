package wooteco.subway.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.controller.dto.LineRequest;
import wooteco.subway.controller.dto.LineResponse;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.service.LineService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/lines")
@RestController
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        final Line line = lineRequest.toLineEntity();
        final Section section = lineRequest.toSectionEntity();
        final Line newLine = lineService.save(line, section);
        final LineResponse lineResponse = new LineResponse(newLine);
        final URI uri = URI.create("/lines/" + newLine.getId());
        return ResponseEntity.created(uri).body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        final List<LineResponse> lineResponses = lineService.findAll()
                                                            .stream()
                                                            .map(LineResponse::new)
                                                            .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        final Line line = lineService.findById(id);
        LineResponse lineResponse = new LineResponse(line);
        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> editLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        final Line line = lineRequest.toLineEntity();
        lineService.update(id, line);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
