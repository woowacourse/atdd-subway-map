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
import wooteco.subway.dto.LineCreateResponse;
import wooteco.subway.dto.LineRequest;

@RestController
public class LineController {

    private final LineDao lineDao;

    public LineController(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineCreateResponse> createLine(@RequestBody LineRequest request) {
        final Line newLine = new Line(request.getName(), request.getColor());

        final List<Line> lines = lineDao.findAll();
        final boolean isExist = lines.stream()
                .anyMatch(line -> line.getName().equals(newLine.getName()));
        if (isExist) {
            throw new IllegalArgumentException("중복된 지하철 노선이 존재합니다.");
        }

        final Long savedId = lineDao.save(newLine);

        return ResponseEntity.created(URI.create("/lines/" + savedId))
                .body(new LineCreateResponse(savedId, newLine.getName(), newLine.getColor()));
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineCreateResponse>> showLines() {
        List<Line> lines = lineDao.findAll();
        final List<LineCreateResponse> lineResponses = lines.stream()
                .map(line -> new LineCreateResponse(line.getId(), line.getName(), line.getColor()))
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineCreateResponse> showLine(@PathVariable Long id) {
        final Line findLine = lineDao.findById(id);
        final LineCreateResponse lineResponse = new LineCreateResponse(id, findLine.getName(), findLine.getColor());

        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineRequest request) {
        lineDao.updateById(new Line(id, request.getName(), request.getColor()));

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineDao.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
