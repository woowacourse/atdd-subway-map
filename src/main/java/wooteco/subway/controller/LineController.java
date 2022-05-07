package wooteco.subway.controller;

import org.springframework.http.MediaType;
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
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createStation(@RequestBody LineRequest lineRequest) {
        LineResponse lineResponse = ControllerDtoAssembler.lineResponseByDTO(lineService.create(ControllerDtoAssembler.lineRequestDTO(lineRequest)));

        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        LineResponse lineResponse = ControllerDtoAssembler.lineResponseByDTO(lineService.findById(id));

        return ResponseEntity.ok(lineResponse);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResponses = lineService.findAll().stream()
                .map(it -> ControllerDtoAssembler.lineResponseByDTO(it))
                .collect(Collectors.toList());

        return ResponseEntity.ok(lineResponses);
    }

    @PutMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.updateById(id, ControllerDtoAssembler.lineRequestDTO(lineRequest));

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
