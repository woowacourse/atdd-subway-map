package wooteco.subway.line;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.exception.LineNotFoundException;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationResponse;

@RestController
public class LineController {
    private LineDao lineDao;

    public LineController() {
        this.lineDao = new LineDao();
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
        LineResponse lineResponse = new LineResponse(createdLine.getId(), createdLine.getName(),
            createdLine.getColor());
        return ResponseEntity.created(URI.create("/lines/" + createdLine.getId()))
            .body(lineResponse);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineDao.findAll();
        List<LineResponse> lineResponses = lines.stream()
            .map(it -> new LineResponse(it.getId(), it.getName(), it.getColor()))
            .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/lines/{id}")
    public ResponseEntity<LineResponse> findLineById(@PathVariable Long id) {
        Line line = lineDao.findLineById(id).orElseThrow(LineNotFoundException::new);
        return ResponseEntity.ok().body(new LineResponse(line.getId(), line.getName(),
            line.getColor()));
    }
}
