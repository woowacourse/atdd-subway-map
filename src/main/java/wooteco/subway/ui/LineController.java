package wooteco.subway.ui;

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

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@RestController
public class LineController {

    private final LineDao lineDao;

    public LineController(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = lineDao.save(lineRequest.getName(), lineRequest.getColor());
        LineResponse lineResponse = new LineResponse(line);
        return ResponseEntity.created(URI.create("/lines/" + line.getId()))
                .body(lineResponse);
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> findAllLine() {
        List<Line> lines = lineDao.findAll();
        List<LineResponse> lineResponses = lines.stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lineResponses);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> findById(@PathVariable Long id) {
        Line line = lineDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 노선이 없습니다."));
        LineResponse lineResponse = new LineResponse(line);
        return ResponseEntity.ok(lineResponse);
    }

    @PutMapping("/lines/{id}")
    public void update(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineDao.update(new Line(id, lineRequest.getName(), lineRequest.getColor()));
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lineDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
