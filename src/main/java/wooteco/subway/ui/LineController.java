package wooteco.subway.ui;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@RestController
public class LineController {

    private final LineDao lineDao;

    public LineController(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = lineDao.save(lineRequest.getName(), lineRequest.getColor());
        LineResponse lineResponse = new LineResponse(line);
        return ResponseEntity.created(URI.create("/lines/" + line.getId()))
                .body(lineResponse);
    }

    // @GetMapping("/lines")
    // public ResponseEntity<List<LineResponse>> findAllLine() {
    //     List<Line> lines = LineDao.findAll();
    //     List<LineResponse> lineResponses = lines.stream()
    //             .map(LineResponse::new)
    //             .collect(Collectors.toList());
    //     return ResponseEntity.ok(lineResponses);
    // }
    //
    // @GetMapping("/lines/{id}")
    // public ResponseEntity<LineResponse> findById(@PathVariable Long id) {
    //     Line line = LineDao.findById(id);
    //     LineResponse lineResponse = new LineResponse(line);
    //     return ResponseEntity.ok(lineResponse);
    // }
    //
    // @PutMapping("/lines/{id}")
    // public void update(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
    //     LineDao.update(id, lineRequest);
    // }
    //
    // @DeleteMapping("/lines/{id}")
    // public ResponseEntity<Void> delete(@PathVariable Long id) {
    //     LineDao.deleteById(id);
    //     return ResponseEntity.noContent().build();
    // }
}
