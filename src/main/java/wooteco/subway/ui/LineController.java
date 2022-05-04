package wooteco.subway.ui;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.dto.LineCreateRequest;
import wooteco.subway.dto.LineCreateResponse;
import wooteco.subway.service.LineService;

@RestController
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineCreateResponse> createLine(@RequestBody LineCreateRequest request) {
        final Long savedId = lineService.save(request);

        return ResponseEntity.created(URI.create("/lines/" + savedId))
                .body(new LineCreateResponse(savedId, request.getName(), request.getColor()));
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineCreateResponse>> showLines() {
        final List<LineCreateResponse> responses = lineService.findAll();

        return ResponseEntity.ok().body(responses);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineCreateResponse> showLine(@PathVariable Long id) {
        final LineCreateResponse response = lineService.findById(id);

        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineCreateRequest request) {
        lineService.updateByLine(id, request);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
