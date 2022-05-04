package wooteco.subway.ui;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@RestController
public class LineController {

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        if (LineDao.findByName(lineRequest.getName()).isPresent()) {
            throw new IllegalArgumentException("중복되는 지하철 노선이 존재합니다.");
        }
        Line newLine = LineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = LineDao.findAll();
        List<LineResponse> lineResponses = lines.stream()
                .map(it -> new LineResponse(it.getId(), it.getName(), it.getColor()))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> findLineById(@PathVariable Long id) {
        Line line = LineDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 지하철 노선이 존재하지 않습니다."));
        LineResponse lineResponse = new LineResponse(line.getId(), line.getName(), line.getColor());
        return ResponseEntity.ok().body(lineResponse);
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        Line station = LineDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 지하철 노선이 존재하지 않습니다."));
        LineDao.delete(station);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<Map<String, String>> handle(RuntimeException exception) {
        return ResponseEntity.badRequest().body(Map.of(
                "message", exception.getMessage()
        ));
    }
}
