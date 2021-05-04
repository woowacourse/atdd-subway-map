package wooteco.subway.line;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LineController {
    private LineDao lineDao;

    public LineController() {
        this.lineDao = new LineDao();
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createStation(@RequestBody LineRequest lineRequest) {
        final String name = lineRequest.getName();
        final String color = lineRequest.getColor();
        if (lineDao.findLineByName(name).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        Line line = new Line(name, color);
        Line createdLine = lineDao.save(line);
        LineResponse lineResponse = new LineResponse(createdLine.getId(), createdLine.getName(),
            createdLine.getColor());
        return ResponseEntity.created(URI.create("/lines/" + createdLine.getId()))
            .body(lineResponse);
    }


}
