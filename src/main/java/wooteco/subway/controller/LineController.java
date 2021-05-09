package wooteco.subway.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.controller.response.LineResponse;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.exception.line.LineNotFoundException;
import wooteco.subway.service.LineService;
import wooteco.subway.service.dto.LineDto;

import javax.validation.Valid;
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

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        LineValidator lineValidator = new LineValidator();
        webDataBinder.addValidators(lineValidator);
    }

    @PostMapping()
    public ResponseEntity<LineResponse> create(@RequestBody @Valid LineRequest lineRequest) {
        LineResponse lineResponse = new LineResponse(lineService.create(lineRequest));
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        final List<LineDto> lines = lineService.findAll();
        final List<LineResponse> lineResponses = lines.stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<LineResponse> showLines(@PathVariable Long id) throws LineNotFoundException {
        LineResponse lineResponse = new LineResponse(lineService.findById(id));
        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<LineResponse> update(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.updateById(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
