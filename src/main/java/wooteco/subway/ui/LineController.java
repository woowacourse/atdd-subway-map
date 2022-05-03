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

    @PostMapping("/lines")
    public ResponseEntity<LineCreateResponse> createLine(@RequestBody LineRequest request) {
        final Line line = new Line(request.getName(), request.getColor());
        final Long savedId = LineDao.save(line);

        return ResponseEntity.created(URI.create("/lines/" + savedId))
                .body(new LineCreateResponse(savedId, line.getName(), line.getColor()));
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineCreateResponse>> showLines() {
        List<Line> lines = LineDao.findAll();
        final List<LineCreateResponse> lineResponses = lines.stream()
                .map(it -> new LineCreateResponse(it.getId(), it.getName(), it.getColor()))
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineCreateResponse> showLine(@PathVariable Long id) {
        final Line findLine = LineDao.findById(id);
        final LineCreateResponse lineResponse = new LineCreateResponse(id, findLine.getName(), findLine.getColor());

        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineRequest request) {
        LineDao.updateById(new Line(id, request.getName(), request.getColor()));

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        LineDao.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/lines")
    public ResponseEntity<Void> deleteAllLine() {
        LineDao.deleteAll();

        return ResponseEntity.noContent().build();
    }
}
