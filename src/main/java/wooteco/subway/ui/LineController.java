package wooteco.subway.ui;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;


@RestController
public class LineController {

    private final LineDao lineDao;

    public LineController(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        String name = lineRequest.getName();
        String color = lineRequest.getColor();

        lineDao.validate(name);
        Line newLine = lineDao.save(name, color);
        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineDao.findAll();
        List<LineResponse> lineResponses = lines.stream()
                .map(it -> new LineResponse(it.getId(), it.getName(), it.getColor()))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line line = lineDao.findById(id);
        return ResponseEntity.ok().body(new LineResponse(line.getId(), line.getName(), line.getColor()));
    }

    @DeleteMapping(value = "/lines/{id}")
    public ResponseEntity<LineResponse> deleteLine(@PathVariable Long id) {
        lineDao.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/lines/{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        String name = lineRequest.getName();
        lineDao.validate(id, name);

        lineDao.update(id, name, lineRequest.getColor());
        return ResponseEntity.ok().build();
    }
}
