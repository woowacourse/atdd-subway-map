package wooteco.subway.line.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.line.controller.dto.LineRequest;
import wooteco.subway.line.controller.dto.LineResponse;
import wooteco.subway.line.repository.dto.LineDto;
import wooteco.subway.line.service.LineService;

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
    public ResponseEntity<LineResponse> createLine(@RequestBody final LineRequest lineRequest) {
        LineDto line = new LineDto(lineRequest.getName(), lineRequest.getColor());
        LineDto newLine = lineService.save(line);
        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResponses = lineService.findAll().stream()
                .map(lineDto -> new LineResponse(lineDto.getId(), lineDto.getName(), lineDto.getColor()))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable final Long id) {
        LineDto lineDto = lineService.findById(id);
        LineResponse lineResponse = new LineResponse(lineDto.getId(), lineDto.getName(), lineDto.getColor(), new ArrayList<>());
        return ResponseEntity.ok().body(lineResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable final Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLine(@RequestBody final LineRequest lineRequest, @PathVariable final Long id) {
        LineDto lineDto = new LineDto(id, lineRequest.getName(), lineRequest.getColor(), new ArrayList<>());
        lineService.update(lineDto);
        return ResponseEntity.ok().build();
    }
}
