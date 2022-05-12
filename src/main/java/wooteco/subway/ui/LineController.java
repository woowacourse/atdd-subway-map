package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.dto.LineWithStationRequest;
import wooteco.subway.dto.LineWithStationResponse;
import wooteco.subway.service.LineService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineWithStationResponse> createLine(@RequestBody final LineWithStationRequest lineRequest) {
        final LineWithStationResponse lineResponse = lineService.save(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping
    public ResponseEntity<List<LineWithStationResponse>> getLines() {
        final List<LineWithStationResponse> lineResponses = lineService.findAll();
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineWithStationResponse> getLine(@PathVariable final Long id) {
        final LineWithStationResponse lineResponse = lineService.findById(id);
        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable final Long id, @RequestBody final LineWithStationRequest lineRequest) {
        lineService.update(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable final Long id) {
        lineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
