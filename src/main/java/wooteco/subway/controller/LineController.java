package wooteco.subway.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.controller.response.LineCreateResponse;
import wooteco.subway.controller.response.LineRetrieveResponse;
import wooteco.subway.controller.request.LineAndSectionCreateRequest;
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

    @PostMapping()
    public ResponseEntity<LineCreateResponse> create(@RequestBody @Valid LineAndSectionCreateRequest lineAndSectionCreateRequest) {
        LineCreateResponse lineResponse = new LineCreateResponse(lineService.create(lineAndSectionCreateRequest));
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineRetrieveResponse>> showLines() {
        final List<LineDto> lines = lineService.findAll();
        final List<LineRetrieveResponse> lineRetrieveRespons = lines.stream()
                .map(LineRetrieveResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineRetrieveRespons);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<LineRetrieveResponse> update(@PathVariable Long id, @RequestBody LineAndSectionCreateRequest lineAndSectionCreateRequest) {
        lineService.updateById(id, lineAndSectionCreateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
