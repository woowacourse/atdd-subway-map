package wooteco.subway.line;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.AppConfig;

import java.net.URI;

@RestController
public class LineController {
    private final LineService lineService = AppConfig.lineService();

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLines(@RequestBody LineRequest lineRequest) {
        LineResponse lineResponse = lineService.createLine(lineRequest.getName(), lineRequest.getColor());
        return ResponseEntity.created(URI.create("/lines" + lineResponse.getId())).body(lineResponse);
    }
}
