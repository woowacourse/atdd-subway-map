package wooteco.subway.controller;

import java.net.URI;
import java.util.ArrayList;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.domain.line.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.service.LineService;

@RestController
public class LineController {

    private final LineService lineService = new LineService();

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line newLine = lineService.createLine(lineRequest.getName(), lineRequest.getColor());
        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(),
            newLine.getColor(), new ArrayList<>());
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

}
