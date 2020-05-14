package wooteco.subway.admin.controller;

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
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.service.LineService;
import wooteco.subway.admin.service.StationService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;
    private final StationService stationService;

    public LineController(final LineService lineService, final StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<Line> createLine(@RequestBody LineRequest lineRequest) {
        Line line = LineRequest.toLine(lineRequest);
        Line savedLine = lineService.save(line);
        return ResponseEntity.created(URI.create("/lines/" + savedLine.getId())).body(savedLine);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> findAllLine() {
        List<LineResponse> lineResponses = lineService.findAll().stream()
                .map(line -> lineService.findLineWithStationsById(line.getId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lineResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> findLine(@PathVariable Long id) {
        return ResponseEntity.ok(lineService.findLineWithStationsById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        Line line = LineRequest.toLine(lineRequest);
        lineService.updateLine(id, line);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{lineId}/stations")
    public ResponseEntity<Void> addStation(@PathVariable Long lineId,
                                     @RequestBody LineStationCreateRequest req) {
        Long preStationId = stationService.findStationId(req.getPreStationName());
        Long stationId = stationService.findStationId(req.getStationName());

        LineStation lineStation = new LineStation(
                preStationId, stationId, req.getDistance(), req.getDuration());

        lineService.addLineStation(lineId, lineStation);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineId}/stations/{stationId}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long lineId, @PathVariable Long stationId) {
        lineService.removeLineStation(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
