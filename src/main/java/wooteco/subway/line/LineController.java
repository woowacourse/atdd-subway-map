package wooteco.subway.line;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

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
        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId()))
                             .body(lineResponse);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineService.findAll();
        List<LineResponse> lineResponses = lines.stream()
                                                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                                                .collect(Collectors.toList());
        return ResponseEntity.ok()
                             .body(lineResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line findLine = lineService.findById(id);
        LineResponse lineResponse = new LineResponse(findLine.getId(), findLine.getName(), findLine.getColor());
        return ResponseEntity.ok()
                             .body(lineResponse);
    }

}
