package wooteco.subway.line;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public final class LineController {

    private final LineDao lineDao;

    public LineController(final LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody final LineRequest lineRequest) {
        final String name = lineRequest.getName();
        final String color = lineRequest.getColor();

        final Long id = lineDao.save(name, color);
        final LineResponse lineResponse = new LineResponse(id, name, color);

        return ResponseEntity.created(URI.create("/lines/" + id)).body(lineResponse);
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<LineResponse> updateLine(@RequestBody final LineRequest lineRequest, @PathVariable final Long id) {
        final String name = lineRequest.getName();
        final String color = lineRequest.getColor();

        lineDao.update(id, name, color);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> showLines() {
        final List<Line> lines = lineDao.findAll();

        final List<LineResponse> lineResponses = lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable final Long id) {
        final Line line = lineDao.findById(id);

        final LineResponse lineResponse = new LineResponse(line.getId(), line.getName(), line.getColor());
        return ResponseEntity.ok().body(lineResponse);
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable final Long id) {
        lineDao.delete(id);
        return ResponseEntity.noContent().build();
    }
}
