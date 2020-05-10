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
import wooteco.subway.admin.dto.request.EdgeAddRequest;
import wooteco.subway.admin.dto.request.LineRequest;
import wooteco.subway.admin.dto.request.LineUpdateRequest;
import wooteco.subway.admin.dto.response.LineResponse;
import wooteco.subway.admin.dto.response.StationsAtLineResponse;
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
        LineResponse persistLine = lineService.save(request);
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


    @GetMapping("/edges")
    public ResponseEntity showEdges() {
        List<StationsAtLineResponse> response = lineService.findAllEdges();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/edges/{lineId}")
    public ResponseEntity createEdge(@PathVariable("lineId") Long lineId, @RequestBody EdgeAddRequest request) {
        StationsAtLineResponse response = lineService.addEdge(lineId, request);

        return ResponseEntity
                .created(URI.create("/edges/" + lineId))
                .body(response);
    }

    @DeleteMapping("/edges/{lineId}/{stationId}")
    public ResponseEntity<Void> deleteEdge(@PathVariable("lineId") Long lineId, @PathVariable("stationId") Long stationId) {
        lineService.removeEdge(lineId, stationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
