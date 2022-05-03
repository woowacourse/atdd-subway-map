package wooteco.subway.ui;

import java.net.URI;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@RestController
public class LineController {

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Optional<Line> wrappedStation = LineDao.findByName(lineRequest.getName());
        if (wrappedStation.isPresent()) {
            throw new IllegalArgumentException("이미 같은 이름의 노선이 존재합니다.");
        }
        Line savedLine = LineDao.saveLine(line);
        return ResponseEntity.created(URI.create("/lines/" + savedLine.getId())).body(LineResponse.of(line));
    }
}
