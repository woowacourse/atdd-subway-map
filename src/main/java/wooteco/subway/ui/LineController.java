package wooteco.subway.ui;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
        Line savedLine = LineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        return ResponseEntity.created(URI.create("/lines")).body(LineResponse.from(savedLine));
    }

    @GetMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        return ResponseEntity.ok(LineResponse.from(LineDao.findById(id)));
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> values = LineDao.findAll();
        List<LineResponse> responses = values.stream()
            .map(LineResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        LineDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
