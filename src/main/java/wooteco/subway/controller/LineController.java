package wooteco.subway.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
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
import wooteco.subway.service.LineService;
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
    public ResponseEntity<LineResponse> createLine(@RequestBody final LineRequest lineRequest) {
        LineServiceDto lineServiceDto = new LineServiceDto(lineRequest.getName(), lineRequest.getColor());
        LineServiceDto createdLineServiceDto = lineService.createLine(lineServiceDto);
        LineResponse lineResponse = new LineResponse(createdLineServiceDto.getId(),
            createdLineServiceDto.getName(), createdLineServiceDto.getColor());

        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId()))
            .body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineServiceDto> lines = lineService.findAll();
        List<LineResponse> lineResponses = lines.stream()
            .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
            .collect(Collectors.toList());

        return ResponseEntity.ok()
            .body(lineResponses);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable final Long id) {
        LineServiceDto lineServiceDto = lineService.findOne(new LineServiceDto(id));
        LineResponse lineResponse = new LineResponse(
            lineServiceDto.getId(),
            lineServiceDto.getName(),
            lineServiceDto.getColor()
        );

        return ResponseEntity.ok()
            .body(lineResponse);

    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> updateLine(@RequestBody final LineRequest lineRequest,
        @PathVariable final Long id) {

        LineServiceDto lineServiceDto = new LineServiceDto(id, lineRequest.getName(), lineRequest.getColor());
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
