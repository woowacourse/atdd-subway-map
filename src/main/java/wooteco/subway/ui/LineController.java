package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.service.LineService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line savedLine = lineService.create(line);
        return ResponseEntity.created(URI.create("/lines/" + savedLine.getId())).body(LineResponse.of(savedLine));
    }

    @GetMapping("/lines")
    public List<LineResponse> getAllLines() {
        List<Line> allLines = lineService.queryAll();
        return allLines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @GetMapping("/lines/{lineId}")
    public LineResponse getLineById(@PathVariable Long lineId) {
        return LineResponse.of(lineService.queryById(lineId));
    }

    @PutMapping("/lines/{lineId}")
    public void updateLine(@PathVariable Long lineId, @RequestBody LineRequest lineRequest) {
        Line newLine = new Line(lineRequest.getName(), lineRequest.getColor());
        lineService.modify(lineId, newLine);
    }

    @DeleteMapping("/lines/{lineId}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long lineId) {
        lineService.remove(lineId);
        return ResponseEntity.noContent().build();
    }
}
