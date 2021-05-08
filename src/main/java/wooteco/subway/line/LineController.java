package wooteco.subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.exception.LineNameDuplicatedException;
import wooteco.subway.exception.LineNotFoundException;
import wooteco.subway.line.dao.LineDao;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class LineController {

    private final LineDao lineDao;

    public LineController(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createStation(@RequestBody LineRequest lineRequest) {
        final String name = lineRequest.getName();
        final String color = lineRequest.getColor();
        if (lineDao.findLineByName(name).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        Line line = new Line(name, color);
        Line createdLine = lineDao.save(line);
        LineResponse lineResponse = LineResponse.of(createdLine);
        return ResponseEntity.created(URI.create("/lines/" + createdLine.getId()))
                .body(lineResponse);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineDao.findAll();
        List<LineResponse> lineResponses = lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/lines/{id}")
    public ResponseEntity<LineResponse> findLineById(@PathVariable Long id) {
        Line line = lineDao.findLineById(id).orElseThrow(LineNotFoundException::new);
        return ResponseEntity.ok().body(LineResponse.of(line));
    }

    @PutMapping(value = "/lines/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineDao.findLineById(id).orElseThrow(LineNotFoundException::new);

        final Optional<Line> lineByName = lineDao.findLineByName(lineRequest.getName());
        if (lineByName.isPresent() && lineByName.get().isNotSameId(id)) {
            throw new LineNameDuplicatedException();
        }

        lineDao.update(id, lineRequest.getName(), lineRequest.getColor());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> removeLine(@PathVariable Long id) {
        lineDao.removeById(id);
        return ResponseEntity.noContent().build();
    }
}
