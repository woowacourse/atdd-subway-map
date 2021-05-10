package wooteco.subway.web.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.domain.line.Line;
import wooteco.subway.service.LineService;
import wooteco.subway.web.dto.LineRequest;
import wooteco.subway.web.dto.LineResponse;

@RestController
@RequestMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> create(@RequestBody @Valid LineRequest lineRequest) {
        Line line = lineService.add(lineRequest.toEntity());

        return ResponseEntity
                .created(URI.create("/lines/" + line.getId()))
                .body(new LineResponse(line));
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> list() {
        List<LineResponse> lineResponses = lineService.findAll()
                .stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity
                .ok(lineResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> find(@PathVariable Long id) {
        Line line = lineService.findById(id);

        return ResponseEntity
                .ok(new LineResponse(line));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id,
            @RequestBody @Valid LineUpdateRequest lineRequest) {
        lineService.update(id, lineRequest.toEntity());

        return ResponseEntity
                .ok()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lineService.delete(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
