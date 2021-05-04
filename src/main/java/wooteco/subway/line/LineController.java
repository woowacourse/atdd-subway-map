package wooteco.subway.line;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.exception.DuplicatedLineNameException;

@RestController
public class LineController {

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        try {
            Line line = new Line(lineRequest.getName(), lineRequest.getColor());
            Line newLine = LineDao.save(line);
            LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(),
                newLine.getColor());
            return ResponseEntity.created(URI.create("/lines/" + newLine.getId()))
                .body(lineResponse);
        } catch (DuplicatedLineNameException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
