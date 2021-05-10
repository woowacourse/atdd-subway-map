package wooteco.subway.line;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {

    private LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> create(@RequestBody final LineRequest lineRequest) {
        final Line line = lineService.create(new Line(lineRequest.getName(), lineRequest.getColor(), lineRequest.getUpStationId(), lineRequest.getDownStationId()));

        final LineResponse lineResponse = new LineResponse(line);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(lineResponse);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> lines() {
        final List<Line> lines = lineService.findAll();

        final List<LineResponse> lineResponses = lines.stream()
                .map(line -> new LineResponse(line))
                .collect(Collectors.toList());

        return ResponseEntity.ok(lineResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> line(@PathVariable final Long id) {
        final Line line = lineService.findById(id);

        final LineResponse lineResponse = new LineResponse(line);
        return ResponseEntity.ok(lineResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> update(@RequestBody final LineRequest lineRequest,
                                               @PathVariable final Long id) {
        lineService.update(new Line(id, lineRequest.getName(), lineRequest.getColor()));

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final Long id) {
        lineService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
