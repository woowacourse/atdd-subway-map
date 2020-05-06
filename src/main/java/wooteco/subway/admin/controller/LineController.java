package wooteco.subway.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.service.LineService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping
public class LineController {

    private final LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity<Line> createLine(@RequestBody LineRequest lineRequest) {
        return new ResponseEntity<>(lineService.save(lineRequest.toLine()), HttpStatus.CREATED);
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> getLines() {
        List<Line> lines = lineService.showLines();
        List<LineResponse> lineResponses = lines.stream()
                .map(line -> new LineResponse(line))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lineResponses);
    }
}
