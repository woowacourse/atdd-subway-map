package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateByNameRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.service.LineService;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLines(@RequestBody @Valid LineRequest request) {
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
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long id,
        @RequestBody @Valid LineRequest request) {
        Line updatedLine = lineService.updateLine(id, request.toLine());

        return ResponseEntity.ok()
            .body(LineResponse.of(updatedLine, lineService.findStationsOf(updatedLine)));
    }

    @GetMapping("/{id}")
    public LineResponse getLine(@PathVariable Long id) {
        Line line = lineService.findLineWithStationsById(id);
        return LineResponse.of(line, lineService.findStationsOf(line));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/stations")
    public ResponseEntity<StationResponse> addLineStation(
        @PathVariable Long id,
        @RequestBody @Valid LineStationCreateByNameRequest request) {
        Long stationId = lineService.addLineStationByName(id, request);

        return ResponseEntity
            .created(URI.create("/lines/" + id + "/stations/" + stationId))
            .build();
    }

    @GetMapping("/{id}/stations")
    public ResponseEntity<List<StationResponse>> getStationsOfLine(@PathVariable Long id) {
        Set<Station> response = lineService.findStationsOf(id);

        return ResponseEntity
                .ok()
                .body(StationResponse.listOf(response));
    }

    @DeleteMapping("/{lineId}/stations/{stationId}")
    public ResponseEntity<Void> removeLineStation(
        @PathVariable Long lineId,
        @PathVariable Long stationId) {
        lineService.removeLineStation(lineId, stationId);

        return ResponseEntity.noContent().build();
    }

}
