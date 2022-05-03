package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

import java.net.URI;

@RestController
@RequestMapping("/lines")
public class LineController {

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line savedLine = LineDao.save(line);
        LineResponse lineResponse = new LineResponse(savedLine.getId(), savedLine.getName(), savedLine.getColor());
        return ResponseEntity.created(URI.create("/lines/" + savedLine.getId())).body(lineResponse);
    }
}
