package wooteco.subway.ui;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineCreateResponse;
import wooteco.subway.dto.LineRequest;

@RestController
public class LineController {

    @PostMapping("/lines")
    public ResponseEntity<LineCreateResponse> createLine(@RequestBody LineRequest request) {
        final Line line = new Line(request.getName(), request.getColor());
        final Long savedId = LineDao.save(line);

        return ResponseEntity.created(URI.create("/lines/" + savedId))
                .body(new LineCreateResponse(savedId, line.getName(), line.getColor()));
    }
}
