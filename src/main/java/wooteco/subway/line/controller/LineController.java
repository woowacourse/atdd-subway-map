package wooteco.subway.line.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.service.LineService;
import wooteco.subway.station.service.StationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/lines")
public class LineController {

    private LineService lineService;
    private StationService stationService;

    public LineController(final LineService lineService, final StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> create(@RequestBody @Valid final LineRequest lineRequest) {
        final LineResponse line = lineService.create(lineRequest);
        final URI responseUri = URI.create("/lines/" + line.getId());

        return ResponseEntity.created(responseUri).body(line);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> update(@PathVariable @Positive final Long id, @RequestBody @Valid final LineRequest lineRequest) {
        lineService.update(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive final Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> line(@PathVariable @Positive final Long id) {
        return ResponseEntity.ok(lineService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> lines() {
        final List<Line> lines = lineService.findAll();
        final List<LineResponse> lineResponses = lines.stream()
                .map(this::lineResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(lineResponses);
    }

    private LineResponse lineResponse(final Line line) {
        final List<Long> stationIdsInLine = lineService.allStationIdInLine(line);
        return new LineResponse(line, stationService.idsToStations(stationIdsInLine));
    }
}
