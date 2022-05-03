package wooteco.subway.ui;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<LineResponse> create(@RequestBody LineRequest lineRequest) {
        if (LineDao.existsByName(lineRequest.getName())) {
            return ResponseEntity.badRequest().build();
        }
        final Line line = new Line(lineRequest.getName(), lineRequest.getColor(), lineRequest.getUpStationId(),
                lineRequest.getDownStationId(), lineRequest.getDistance());
        final Line newLine = LineDao.save(line);

        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).build();
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = LineDao.findAll();
        List<LineResponse> lineResponses = lines.stream()
                .map(it -> new LineResponse(it.getId(), it.getName(), it.getColor()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lineResponses);
    }

    @GetMapping(value = "/lines/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        if (LineDao.notExistsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        Line line = LineDao.findById(id);

        return ResponseEntity.ok(new LineResponse(line.getId(), line.getName(), line.getColor()));
    }
}
