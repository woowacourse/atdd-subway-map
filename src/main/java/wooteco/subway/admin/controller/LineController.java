package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateByNameRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.service.LineService;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity createLines(@RequestBody LineRequest request) {
        Line line = lineService.save(request.toLine());

        return ResponseEntity
                .created(URI.create("/lines/" + line.getId()))
                .body(LineResponse.of(line, new HashSet<>()));
    }

    @GetMapping
    public List<LineResponse> getLines() {
        List<Line> lines = lineService.showLines();
        return lines.stream()
                .map(line -> LineResponse.of(line, lineService.findStationsOf(line.getId())))
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest request) {
        LineResponse response = lineService.updateLine(id, request.toLine());

        return ResponseEntity.ok()
                .body(response);
    }

    @GetMapping("/{id}")
    public LineResponse getLine(@PathVariable Long id) {
        return lineService.findLineWithStationsById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/stations")
    public ResponseEntity addLineStation(
            @PathVariable Long id,
            @RequestBody LineStationCreateByNameRequest request) {
        Long stationId = lineService.addLineStationByName(id, request);

        return ResponseEntity
                .created(URI.create("/lines/" + id + "/stations/" + stationId))
                .build();
    }

    @GetMapping("/{id}/stations")
    public ResponseEntity getStationsOfLine(@PathVariable Long id) {
        Set<Station> response = lineService.findStationsOf(id);

        return ResponseEntity
                .ok()
                .body(StationResponse.listOf(response));
    }

    @DeleteMapping("/{lineId}/stations/{stationId}")
    public ResponseEntity removeLineStation(
            @PathVariable Long lineId,
            @PathVariable Long stationId) {
        lineService.removeLineStation(lineId, stationId);

        return ResponseEntity.noContent().build();
    }

}
