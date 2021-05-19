package wooteco.subway.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import wooteco.subway.controller.dto.LineEditRequest;
import wooteco.subway.controller.dto.LineRequest;
import wooteco.subway.controller.dto.LineResponse;
import wooteco.subway.domain.Line;
import wooteco.subway.service.LineService;

@Validated
@RequestMapping("/lines")
@RestController
public class LineController {

    private final LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@Valid @RequestBody LineRequest lineRequest) {
        final Line newLine = lineService.save(lineRequest);
        final URI uri = URI.create("/lines/" + newLine.getId());
        return ResponseEntity.created(uri).body(LineResponse.from(newLine));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        final List<LineResponse> lineResponses = lineService.findAll()
                                                            .stream()
                                                            .map(LineResponse::from)
                                                            .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        final Line line = lineService.findById(id);
        return ResponseEntity.ok().body(LineResponse.from(line));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> editLine(@PathVariable Long id, @Valid @RequestBody LineEditRequest lineEditRequest) {
        lineService.update(id, lineEditRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
