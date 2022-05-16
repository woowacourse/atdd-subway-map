package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.service.LineService;

import javax.validation.Valid;
import java.net.URI;
import java.util.*;

@RequestMapping("/lines")
@RestController
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping()
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse lineResponse = lineService.save(lineRequest);

        return ResponseEntity
                .created(URI.create("/lines/" + lineResponse.getId()))
                .body(lineResponse);
    }

    @GetMapping()
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResponses = lineService.findAll();

        return ResponseEntity
                .ok()
                .body(lineResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> searchLine(@PathVariable Long id) {
        LineResponse lineResponse = lineService.findById(id);

        return ResponseEntity
                .ok()
                .body(lineResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> editLine(@PathVariable Long id, @Valid @RequestBody LineRequest lineRequest) {
        lineService.edit(id, lineRequest.getName(), lineRequest.getColor());

        return ResponseEntity
                .ok()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.delete(id);

        return ResponseEntity
                .noContent()
                .build();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Void> lineNotFound() {
        return ResponseEntity
                .noContent()
                .build();
    }
}
