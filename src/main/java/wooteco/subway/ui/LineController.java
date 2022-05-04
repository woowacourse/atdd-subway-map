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

    public LineController() {
        this.lineDao = new LineDao();
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line createdLine = lineDao.save(line);
        LineResponse lineResponse = new LineResponse(createdLine.getId(), createdLine.getName(), createdLine.getColor());
        return ResponseEntity.created(URI.create("/lines/" + createdLine.getId())).body(lineResponse);
    }
}
