package wooteco.subway.web.controller;

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
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.line.dao.LineDao;
import wooteco.subway.web.dto.LineRequest;
import wooteco.subway.web.dto.LineResponse;

@RestController
@RequestMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
public class LineController {

    private final LineDao lineDao;

    public LineController(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Long id = lineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        LineResponse lineResponse = new LineResponse(id,
                lineRequest.getName(),
                lineRequest.getColor());

        return ResponseEntity
                .created(URI.create("/lines/" + id))
                .body(lineResponse);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineDao.findAll();
        List<LineResponse> lineResponses = lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .collect(Collectors.toList());

        return ResponseEntity
                .ok()
                .body(lineResponses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id,
            @RequestBody LineRequest lineRequest) {
        Line line = lineRequest.toEntity();
        lineDao.update(id, line);

        return ResponseEntity
                .ok()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineDao.delete(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
