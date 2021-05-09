package wooteco.subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        long upStationId = lineRequest.getUpStationId();
        long downStationId = lineRequest.getDownStationId();
        String lineName = lineRequest.getName();
        String lineColor = lineRequest.getColor();
        int distance = lineRequest.getDistance();

        Line line = lineService.createLine(upStationId, downStationId, lineName, lineColor, distance);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(new LineResponse(line));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResponses = lineService.showLines().stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable long id) {
        Line line = lineService.showLine(id);
        return ResponseEntity.ok().body(new LineResponse(line));
    }

    @PutMapping("{id}")
    public ResponseEntity<String> updateLine(@RequestBody LineRequest lineRequest, @PathVariable long id) {
        String lineName = lineRequest.getName();
        String lineColor = lineRequest.getColor();
        lineService.updateLine(id, lineName, lineColor);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteLine(@PathVariable long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }
}
