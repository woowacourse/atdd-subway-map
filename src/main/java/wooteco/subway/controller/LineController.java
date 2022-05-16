package wooteco.subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.controller.dto.ControllerDtoAssembler;
import wooteco.subway.controller.dto.line.LineRequest;
import wooteco.subway.controller.dto.line.LineResponse;
import wooteco.subway.service.LineService;

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
    public ResponseEntity<LineResponse> createStation(@RequestBody LineRequest lineRequest) {
        LineResponse lineResponse = ControllerDtoAssembler.lineResponseByDto(lineService.create(ControllerDtoAssembler.lineRequestDto(lineRequest)));

        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> findLine(@PathVariable Long id) {
        LineResponse lineResponse = ControllerDtoAssembler.lineResponseByDto(lineService.findById(id));

        return ResponseEntity.ok(lineResponse);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> findLines() {
        List<LineResponse> lineResponses = lineService.findAll().stream()
                .map(ControllerDtoAssembler::lineResponseByDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(lineResponses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.updateById(id, ControllerDtoAssembler.lineRequestDto(lineRequest));

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
