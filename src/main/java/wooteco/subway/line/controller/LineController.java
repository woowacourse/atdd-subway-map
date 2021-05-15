package wooteco.subway.line.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.service.LineService;
import wooteco.subway.station.dto.StationResponse;
import wooteco.subway.station.service.StationService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<LineResponse> create(@RequestBody final LineRequest lineRequest) {
        final LineResponse lineResponse = lineService.create(lineRequest);
        final URI responseUrl = URI.create("/lines/" + lineResponse.getId());

        return ResponseEntity.created(responseUrl).body(lineResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> update(@PathVariable final Long id, @RequestBody final LineRequest lineRequest) {
        lineService.update(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> lines() {
        final List<Line> lines = lineService.findAll();
        final List<LineResponse> lineResponses = lines.stream()
                .map(this::lineResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(lineResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> line(@PathVariable final Long id) {
        final Line line = lineService.findById(id);
        return ResponseEntity.ok(lineResponse(line));
    }

    private LineResponse lineResponse(final Line line) {
        final List<Long> stationIds = lineService.allStationIdInLine(line.getId());
        return new LineResponse(line, stationResponses(stationIds));
    }

    private List<StationResponse> stationResponses(final List<Long> stationIds) {
        return stationIds.stream()
                .map(stationId -> new StationResponse(stationService.findById(stationId)))
                .collect(Collectors.toList());
    }
}
