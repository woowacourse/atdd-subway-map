package wooteco.subway.controller.apis;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.domain.line.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.service.LineService;

import java.net.URI;
import java.util.ArrayList;
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
        Line savedLine = lineService.createLine(lineRequest.getName(), lineRequest.getColor());
        LineResponse lineResponse = new LineResponse(savedLine.getId(), savedLine.getName(), savedLine.getColor(), new ArrayList<>());
        URI uri = URI.create("/lines/" + savedLine.getId());
        return ResponseEntity.created(uri)
                .body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResponses = lineService.findAll()
                .stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor(), new ArrayList<>()))
                .collect(Collectors.toList());
        return ResponseEntity.ok()
                .body(lineResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line line = lineService.findById(id);
        LineResponse lineResponse = new LineResponse(line.getId(), line.getName(), line.getColor(), new ArrayList<>());
        return ResponseEntity.ok()
                .body(lineResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> editLine(@PathVariable long id, @RequestBody LineRequest lineRequest) {
        lineService.editLine(id, lineRequest.getName(), lineRequest.getColor());
        return ResponseEntity.ok()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent()
                .build();
    }
}
