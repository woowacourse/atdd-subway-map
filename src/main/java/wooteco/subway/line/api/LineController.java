package wooteco.subway.line.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.line.api.dto.LineDetailsResponse;
import wooteco.subway.line.api.dto.LineRequest;
import wooteco.subway.line.api.dto.LineResponse;
import wooteco.subway.line.api.dto.LineUpdateRequest;
import wooteco.subway.line.service.LineService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineDetailsResponse> createLine(@RequestBody @Valid LineRequest lineRequest) {
        LineDetailsResponse newLine = lineService.createLine(lineRequest);
        String uri = "/lines/" + newLine.getId();
        return ResponseEntity.created(URI.create(uri))
                .body(newLine);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResponses = lineService.findAll();
        return ResponseEntity.ok(lineResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineDetailsResponse> showLine(@PathVariable Long id) {
        return ResponseEntity.ok(lineService.showLineById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity updateLine(@PathVariable Long id, @RequestBody @Valid LineUpdateRequest lineUpdateRequest) {
        lineService.update(id, lineUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
