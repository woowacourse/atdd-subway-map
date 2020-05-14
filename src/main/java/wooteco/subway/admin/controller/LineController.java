package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.service.LineService;

import java.net.URI;

@RestController
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity createLine(@RequestBody LineRequest view) {
        Line line = view.toLine();
        try {
            Line persistLine = lineService.save(line);
            return ResponseEntity
                    .created(URI.create("/lines/" + persistLine.getId()))
                    .body(LineResponse.of(persistLine));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/lines")
    public ResponseEntity showLines() {
        return ResponseEntity.ok().body(lineService.showLines());
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> findLineById(@PathVariable Long id) {
        return ResponseEntity.ok().body(lineService.showLine(id));
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineRequest view) {
        LineResponse lineResponse = lineService.updateLine(id, view.toLine());
        return ResponseEntity.ok().body(lineResponse);
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lines/{id}/stations")
    public ResponseEntity<LineResponse> addLineStation(@PathVariable Long id,
                                                       @RequestBody LineStationCreateRequest lineStationCreateRequest) {
        LineResponse lineResponse = lineService.addLineStation(id, lineStationCreateRequest);
        return ResponseEntity.created(URI.create("/line/" + id + "/stations"))
                .body(lineResponse);
    }

    @GetMapping("/lines/{id}/stations")
    public ResponseEntity<LineResponse> findLine(@PathVariable Long id) {
        LineResponse lineResponse = lineService.findLineWithStationsById(id);
        return ResponseEntity.ok()
                .body(lineResponse);
    }

    @DeleteMapping("/lines/{lineId}/stations/{stationId}")
    public ResponseEntity deleteLineStation(@PathVariable Long lineId,
                                            @PathVariable Long stationId) {
        lineService.removeLineStation(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
