package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.List;
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
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.service.LineService;

@RequestMapping("/lines")
@RestController
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> findLine(@PathVariable Long id) {
        Line line = lineService.findById(id);
        List<Station> stations = lineService.findStationsByLineId(line.getLineStationsId());

        return ResponseEntity
            .ok()
            .body(LineResponse.of(line, stations));
    }

    @GetMapping()
    public List<LineResponse> showLines() {
        List<Line> lines = lineService.showLines();

        return lines.stream()
                .map(line -> LineResponse.of(line, lineService.findStationsByLineId(line.getLineStationsId())))
                .collect(Collectors.toList());
    }

    @PostMapping()
    public ResponseEntity<Void> createLine(@RequestBody @Valid LineRequest lineRequest) {
        Line line = lineService.save(lineRequest.toLine());

        return ResponseEntity
            .created(URI.create(String.valueOf(line.getId())))
            .build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody @Valid LineRequest lineRequest) {
        lineService.updateLine(id, lineRequest.toLine());

        return ResponseEntity
            .noContent()
            .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);

        return ResponseEntity
            .noContent()
            .build();
    }

    @GetMapping("/{id}/stations")
    public ResponseEntity<List<LineStation>> showLineStations(@PathVariable Long id) {
        Line line = lineService.findById(id);

        return ResponseEntity
            .ok()
            .body(line.getStations());
    }

    @PostMapping("/{id}/stations")
    public ResponseEntity<LineResponse> createLineStation(@PathVariable Long id, @RequestBody LineStationCreateRequest lineStationCreateRequest) {
        Line line = lineService.addLineStation(id, lineStationCreateRequest);

        return ResponseEntity
            .created(URI.create("/lines/" + line.getId()))
            .build();
    }

    @DeleteMapping("/{lineId}/stations/{stationId}")
    public ResponseEntity<Void> deleteLineStation(@PathVariable Long lineId, @PathVariable Long stationId) {
        lineService.removeLineStation(lineId, stationId);
        return ResponseEntity
            .noContent()
            .build();
    }
}
