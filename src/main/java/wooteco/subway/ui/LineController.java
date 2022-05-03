package wooteco.subway.ui;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.dao2.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineBasicResponse;
import wooteco.subway.dto.LineRequest;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineDao lineDao;

    public LineController(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @PostMapping
    public ResponseEntity<LineBasicResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line newLine = lineDao.save(line);
        LineBasicResponse lineResponse = new LineBasicResponse(newLine.getId(), newLine.getName(), newLine.getColor());
        URI location = URI.create("/stations/" + newLine.getId());
        return ResponseEntity.created(location).body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineBasicResponse>> showLines() {
        List<Line> lines = lineDao.findAll();
        List<LineBasicResponse> linesResponse = lines.stream()
                .map(it -> new LineBasicResponse(it.getId(), it.getName(), it.getColor()))
                .collect(Collectors.toUnmodifiableList());
        return ResponseEntity.ok().body(linesResponse);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineBasicResponse> showLine(@PathVariable Long id) {
        Line line = lineDao.findById(id);
        LineBasicResponse linesResponse = new LineBasicResponse(line.getId(), line.getName(), line.getColor());
        return ResponseEntity.ok().body(linesResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        Line line = new Line(id, lineRequest.getName(), lineRequest.getColor());
        lineDao.update(line);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
