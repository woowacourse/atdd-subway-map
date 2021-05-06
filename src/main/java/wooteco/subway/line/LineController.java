package wooteco.subway.line;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LineController {

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line newLine = LineDao.save(line);
        LineResponse lineResponse = new LineResponse(newLine);
        return ResponseEntity.created(URI.create("/stations/" + newLine.getId()))
                             .body(lineResponse);
    }
}
