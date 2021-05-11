package wooteco.subway.controller;

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
import wooteco.subway.controller.dto.request.UpdateLineRequest;
import wooteco.subway.service.LineService;
import wooteco.subway.service.dto.CreateLineDto;
import wooteco.subway.service.dto.LineServiceDto;
import wooteco.subway.controller.dto.request.LineRequest;
import wooteco.subway.controller.dto.response.LineResponse;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@Valid @RequestBody final LineRequest lineRequest) {
        CreateLineDto createLineDto = CreateLineDto.from(lineRequest);
        LineServiceDto createdLineServiceDto = lineService.createLine(createLineDto);
        LineResponse lineResponse = LineResponse.from(createdLineServiceDto);

        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId()))
            .body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineServiceDto> lines = lineService.findAll();
        List<LineResponse> lineResponses = lines.stream()
            .map(LineResponse::from)
            .collect(Collectors.toList());

        return ResponseEntity.ok(lineResponses);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable final Long id) {
        LineServiceDto lineServiceDto = lineService.findOne(new LineServiceDto(id));
        LineResponse lineResponse = LineResponse.from(lineServiceDto);

        return ResponseEntity.ok(lineResponse);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateLine(
        @Valid @RequestBody final UpdateLineRequest updateLineRequest, @PathVariable final Long id) {

        LineServiceDto lineServiceDto = LineServiceDto.from(id, updateLineRequest);
        lineService.update(lineServiceDto);

        return ResponseEntity.ok()
            .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable final Long id) {
        lineService.delete(new LineServiceDto(id));

        return ResponseEntity.noContent()
            .build();
    }
}
