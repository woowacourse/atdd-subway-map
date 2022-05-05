package wooteco.subway.ui;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineDao lineDao;

    public LineController(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line newLine = lineDao.insert(line);
        LineResponse lineResponse = new LineResponse(newLine);
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = Collections.unmodifiableList(lineDao.findAll());
        List<LineResponse> lineResponses = lines.stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lineResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line line = lineDao.findById(id);
        LineResponse lineResponse = new LineResponse(line);
        return ResponseEntity.ok(lineResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineDao.update(id, lineRequest.getName(), lineRequest.getColor());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineDao.delete(id);
        return ResponseEntity.noContent().build();
    }
}
