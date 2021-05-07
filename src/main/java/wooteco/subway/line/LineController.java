package wooteco.subway.line;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
        final Line line = LineDao.findById(id);
        LineResponse lineResponse = new LineResponse(line.getId(), line.getName(), line.getColor());
        return ResponseEntity.ok().body(lineResponse);
    }
}
