package wooteco.subway.line;

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

@RequestMapping("/lines")
@RestController
public class LineController {

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        final Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        final Line newLine = LineDao.save(line);
        final LineResponse lineResponse = new LineResponse(newLine);
        return ResponseEntity.created(URI.create("/stations/" + newLine.getId()))
                             .body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        final List<Line> lines = LineDao.findAll();
        final List<LineResponse> lineResponses = lines.stream()
                                                      .map(line -> new LineResponse(line.getId(),
                                                              line.getName(), line.getColor()))
                                                      .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        final Line line = LineDao.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 ID에 해당하는 노선이 존재하지 않습니다."));
        LineResponse lineResponse = new LineResponse(line.getId(), line.getName(), line.getColor());
        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> editLine(@PathVariable Long id,
                                         @RequestBody LineRequest lineRequest) {
        final Line beforeLine = LineDao.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 ID에 해당하는 노선이 존재하지 않습니다."));
        final Line afterLine = new Line(id, lineRequest.getName(), lineRequest.getColor());
        LineDao.update(beforeLine, afterLine);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        LineDao.delete(id);
        return ResponseEntity.noContent().build();
    }
}
