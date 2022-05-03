package wooteco.subway.ui;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
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
    public ResponseEntity<LineResponse> createLine(@RequestBody @Valid LineRequest lineRequest) {
        Line line = lineRequest.toEntity();
        Line newLine = lineDao.save(line);
        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineDao.findAll();
        List<LineResponse> lineResponses = lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line line = lineDao.findById(id);
        LineResponse lineResponse = new LineResponse(id, line.getName(), line.getColor());
        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        Line line = lineRequest.toEntity();
        lineDao.updateById(id, line);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
