package wooteco.subway.line;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LineController {

    private static final LineService lineService = LineService.getInstance();

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        try {
            LineResponse lineResponse = lineService.createLine(lineRequest);
            return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
