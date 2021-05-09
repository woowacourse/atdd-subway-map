package wooteco.subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;

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
    public ResponseEntity<LineResponse> create(@RequestBody LineRequest lineRequest) {
        Line createdLine = lineService.create(lineRequest.lineWithoutId());
        LineResponse lineResponse = LineResponse.of(createdLine);

        return ResponseEntity.created(URI.create("/lines/" + createdLine.getId()))
                .body(lineResponse);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showAll() {
        List<Line> lines = lineService.showAll();
        List<LineResponse> lineResponses = lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/lines/{id}")
    public ResponseEntity<LineResponse> findById(@PathVariable Long id) {
        Line line = lineService.findById(id);

        return ResponseEntity.ok().body(LineResponse.of(line));
    }

    @PutMapping(value = "/lines/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.findById(id);
        lineService.update(id, lineRequest.lineWithoutId());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        lineService.removeById(id);

        return ResponseEntity.noContent().build();
    }
}
