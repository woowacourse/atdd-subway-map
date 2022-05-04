package wooteco.subway.ui;

import java.net.URI;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.application.LineService;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService = new LineService();

    @PostMapping
    public ResponseEntity<LineResponse> createLines(@RequestBody LineRequest lineRequest) {
        Line line = lineService.save(lineRequest.getName(), lineRequest.getColor());
        LineResponse lineResponse = new LineResponse(line.getId(), line.getName(), line.getColor());
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(lineResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line line = LineDao.findById(id).get();
        LineResponse lineResponse = new LineResponse(line.getId(), line.getName(), line.getColor());
        return ResponseEntity.ok(lineResponse);
    }
}
