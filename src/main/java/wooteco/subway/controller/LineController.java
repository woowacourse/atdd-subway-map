package wooteco.subway.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.controller.dto.LineEditRequest;
import wooteco.subway.controller.dto.LineRequest;
import wooteco.subway.controller.dto.LineResponse;
import wooteco.subway.controller.dto.StationResponse;
import wooteco.subway.domain.Line;
import wooteco.subway.service.LineService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

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
        final Line newLine = lineService.save(lineRequest.toLineEntity(), lineRequest.toSectionEntity());
        final LineResponse lineResponse = convertLineToLineResponse(newLine);
        final URI uri = URI.create("/lines/" + newLine.getId());
        return ResponseEntity.created(uri).body(lineResponse);
    }

    private LineResponse convertLineToLineResponse(final Line line) {
        final List<StationResponse> stationResponses = lineService.findStationsByLineId(line.getId())
                                                                  .stream()
                                                                  .map(StationResponse::new)
                                                                  .collect(Collectors.toList());

        return new LineResponse(line, stationResponses);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        final List<Line> lines = lineService.findAll();
        final List<LineResponse> lineResponses = lines.stream()
                                                      .map(this::convertLineToLineResponse)
                                                      .collect(Collectors.toList());

        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        final Line line = lineService.findById(id);
        final LineResponse lineResponse = convertLineToLineResponse(line);
        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> editLine(@PathVariable Long id, @Valid @RequestBody LineEditRequest lineEditRequest) {
        final Line line = lineEditRequest.toEntity();
        lineService.update(id, line);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
