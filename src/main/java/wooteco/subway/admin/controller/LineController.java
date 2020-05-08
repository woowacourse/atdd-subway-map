package wooteco.subway.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationAddRequest;
import wooteco.subway.admin.dto.LineStationResponse;
import wooteco.subway.admin.dto.LineUpdateRequest;
import wooteco.subway.admin.service.LineService;

import java.net.URI;
import java.util.List;

@RestController
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity createLine(@RequestBody LineRequest request) {
        Line line = request.toLine();
        LineResponse persistLine = lineService.save(line);
        return ResponseEntity
                .created(URI.create("/stations/" + persistLine.getId()))
                .body(persistLine);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity showLine(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(lineService.findLine(id));
    }

    @GetMapping("/lines")
    public ResponseEntity showLines() {
        return ResponseEntity.ok().body(lineService.showLines());
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity updateLine(@PathVariable("id") Long id, @RequestBody LineUpdateRequest request) {
        Line line = request.toLine();
        return ResponseEntity.ok().body(lineService.updateLine(id, line));
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable("id") Long id) {
        lineService.deleteLineById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/lineStations")
    public ResponseEntity showLineStations() {
        List<LineStationResponse> response = lineService.findAllLineStations();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/lineStations")
    public ResponseEntity createLineStations(@RequestBody LineStationAddRequest request) {
        LineStationResponse response = lineService.addLineStation(1L, request);

        return ResponseEntity
                .created(URI.create("/lineStations/" + 1))
                .body(response);
    }

    @DeleteMapping("/lineStations/{id}")
    public ResponseEntity<Void> deleteLineStation(@PathVariable("id") Long lineId, @RequestBody Long stationId) {
        lineService.removeLineStation(lineId, stationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
