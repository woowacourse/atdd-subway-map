package wooteco.subway.ui;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line newLine = LineDao.save(line);
        LineResponse lineResponse = new LineResponse(line);
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId()))
                .body(lineResponse);
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> findAllLine() {
        List<Line> lines = LineDao.findAll();
        List<LineResponse> lineResponses = lines.stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lineResponses);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> findById(@PathVariable Long id) {
        Line line = LineDao.findById(id);
        LineResponse lineResponse = new LineResponse(line);
        return ResponseEntity.ok(lineResponse);
    }


}
