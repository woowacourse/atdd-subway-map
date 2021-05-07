package wooteco.subway.line;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {

    private LineRepository lineRepository;

    public LineController(final LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @PostMapping
    public ResponseEntity<LineResponse> create(@RequestBody final LineRequest lineRequest) {
        final Line lineToSave = new Line(lineRequest.getName(), lineRequest.getColor());

        final Line line = lineRepository.save(lineToSave);
        final LineResponse lineResponse = lineResponseById(line.getId());

        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(lineResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> line(@PathVariable final Long id) {
        final LineResponse lineResponse = lineResponseById(id);
        return ResponseEntity.ok().body(lineResponse);
    }

    private LineResponse lineResponseById(final Long id) {
        final Line line = lineRepository.findById(id);
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> lines() {
        final List<Line> lines = lineRepository.findAll();

        final List<LineResponse> lineResponses = lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(lineResponses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> updateLine(@RequestBody final LineRequest lineRequest, @PathVariable final Long id) {
        final Line line = new Line(id, lineRequest.getName(), lineRequest.getColor());
        lineRepository.save(line);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable final Long id) {
        final Line line = lineRepository.findById(id);
        lineRepository.delete(line);

        return ResponseEntity.noContent().build();
    }
}
