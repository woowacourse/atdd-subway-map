package wooteco.subway.line;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Collections;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    public LineController() {
        this.lineService = new LineService();
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line newLine = lineService.createLine(lineRequest.getName(), lineRequest.getColor());
        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(),
                newLine.getColor(), Collections.emptyList());
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId()))
                             .body(lineResponse);
    }

}
