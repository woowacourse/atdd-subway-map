package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
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

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @GetMapping()
    public ResponseEntity<List<LineResponse>> getLines() {
        return ResponseEntity.ok().body(LineResponse.listOf(lineService.showLines()));
    }

    @GetMapping("/detail")
    public ResponseEntity<List<LineResponse>> getLinesDetail() {
        List<Line> lines = lineService.showLines();
        List<LineResponse> linesDetail = lines.stream()
                .map(line -> LineResponse.of(line, lineService.findStationsByLineId(line.getId())))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(linesDetail);
    }

    @PostMapping()
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        if(lineService.contains(lineRequest.getName())) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        Line persistLine = lineService.save(lineRequest.toLine());

        return ResponseEntity.created(URI.create("/lines/" + persistLine.getId()))
                .body(LineResponse.of(persistLine));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> getLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(LineResponse.of(lineService.findById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        Line persistLine = lineService.updateLine(id, lineRequest.toLine());
        return ResponseEntity.ok().body(LineResponse.of(persistLine));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/stations")
    public ResponseEntity<List<LineStation>> getLineStations(@PathVariable Long id) {
        Line line = lineService.findById(id);
        return ResponseEntity.ok().body(line.getStations());
    }

    @PostMapping("/{id}/stations")
    public ResponseEntity<LineResponse> createLineStation(@PathVariable Long id, @RequestBody LineStationCreateRequest lineStationCreateRequest) {
        Line persistLine = lineService.addLineStation(id, lineStationCreateRequest);
        return ResponseEntity.ok().body(LineResponse.of(persistLine));
    }

    @DeleteMapping("/{lineId}/stations/{stationId}")
    public ResponseEntity<Void> deleteLineStation(@PathVariable Long lineId, @PathVariable Long stationId) {
        lineService.removeLineStation(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
