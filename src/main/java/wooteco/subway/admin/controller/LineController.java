package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.List;

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

    @PostMapping
    public ResponseEntity<Long> createLine(@Valid @RequestBody LineRequest lineRequest) {

        Line line = lineRequest.toLine();
        Line persistLine = lineService.save(line);
        return ResponseEntity
            .created(URI.create("/lines/" + persistLine.getId()))
            .body(persistLine.getId());
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.showLines());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> findLineById(@PathVariable Long id) {
        return ResponseEntity.ok().body(LineResponse.of(lineService.showLine(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id,
        @RequestBody LineRequest view) {
        lineService.updateLine(id, view.toLine());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/stations")
    public ResponseEntity<Void> addLineStation(@PathVariable Long id,
        @RequestBody LineStationCreateRequest lineStationCreateRequest) {
        lineService.addLineStation(id, lineStationCreateRequest);
        return ResponseEntity.created(URI.create("/line/" + id + "/stations")).build();
    }

    @GetMapping("/{id}/stations")
    public ResponseEntity<LineResponse> findLine(@PathVariable Long id) {
        LineResponse lineResponse = lineService.findLineWithStationsById(id);
        return ResponseEntity.ok().body(lineResponse);
    }

    @DeleteMapping("/{lineId}/stations/{stationId}")
    public ResponseEntity<Void> deleteLineStation(@PathVariable Long lineId,
        @PathVariable Long stationId) {
        lineService.removeLineStation(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
