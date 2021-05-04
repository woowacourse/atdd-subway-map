package wooteco.subway.line;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class LineController {

    private final LineService lineService = new LineService();

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse newLine = lineService.save(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(newLine);
    }

}
