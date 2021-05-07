package wooteco.subway.line;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineDao lineDao;

    public LineController(final LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @PostMapping
    public ResponseEntity<LineResponse> create(@RequestBody final LineRequest lineRequest) {
        final String name = lineRequest.getName();
        final String color = lineRequest.getColor();

        if (lineDao.isDuplicatedName(name)) {
            throw new LineException("이미 존재하는 노선 이름입니다.");
        }

        final Long id = lineDao.save(name, color);
        final LineResponse lineResponse = lineResponseById(id);

        return ResponseEntity.created(URI.create("/lines/" + id)).body(lineResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> line(@PathVariable final Long id) {
        final LineResponse lineResponse = lineResponseById(id);
        return ResponseEntity.ok().body(lineResponse);
    }

    private LineResponse lineResponseById(final Long id) {
        final Line line = lineDao.findById(id);
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> lines() {
        final List<Line> lines = lineDao.findAll();

        final List<LineResponse> lineResponses = lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(lineResponses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> updateLine(@RequestBody final LineRequest lineRequest, @PathVariable final Long id) {
        final String name = lineRequest.getName();
        final String color = lineRequest.getColor();

        lineDao.update(id, name, color);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable final Long id) {
        lineDao.delete(id);
        return ResponseEntity.noContent().build();
    }
}
