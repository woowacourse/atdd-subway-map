package wooteco.subway.ui;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.service.LineService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping()
    public ResponseEntity<LineResponse> createLine(@RequestBody final LineRequest lineRequest) {
        final Line line = lineRequest.toEntity();
        final Line newLine = lineService.createLine(line);
        final LineResponse lineResponse = LineResponse.from(newLine);

        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        final List<Line> lines = lineService.getAllLines();
        final List<LineResponse> lineResponses = lines.stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable final Long id) {
        final Line line = lineService.getLineById(id);
        final LineResponse lineResponse = LineResponse.from(line);

        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateLine(@PathVariable final Long id, @RequestBody final LineRequest lineRequest) {
        final Line line = lineRequest.toEntity();
        lineService.update(id, line);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable final Long id) {
        lineService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
