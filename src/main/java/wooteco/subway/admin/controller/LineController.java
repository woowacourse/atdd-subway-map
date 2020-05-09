package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.dto.LineIdResponse;
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
    public ResponseEntity save(@RequestBody LineRequest lineRequest) {
        Long id = lineService.save(lineRequest.toLine())
                .getId();
        return ResponseEntity
                .created(URI.create("/lines/" + id))
                .body(new LineIdResponse(id));
    }

    @GetMapping("/lines")
    public ResponseEntity showLines() {
        return ResponseEntity.ok(LineResponse.listOf(lineService.showLines()));
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity findLineById(@PathVariable Long id) {
        return ResponseEntity.ok()
                .body(LineResponse.of(lineService.findLineById(id)));
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.updateLine(id, lineRequest.toLine());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLineById(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lines/{id}/stations")
    public ResponseEntity addLineStation(@PathVariable Long id, @RequestBody LineStationCreateRequest lineStationCreateRequest) {
        lineService.addLineStation(id, lineStationCreateRequest);
        return ResponseEntity
                .created(URI.create("/lines/" + id + "/stations"))
                .build();
    }

    @DeleteMapping("/lines/{lineId}/stations/{stationId}")
    public ResponseEntity removeLineStation(@PathVariable Long lineId, @PathVariable Long stationId) {
        lineService.removeLineStation(lineId, stationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/lines/{id}/stations")
    public ResponseEntity findLineWithStationsById(@PathVariable Long id) {
        return ResponseEntity.ok().body(lineService.findLineWithStationsById(id));
    }

    @GetMapping("/lines/stations")
    public ResponseEntity findAllLineWithStations() {
        return ResponseEntity.ok().body(lineService.findAllLineWithStations());
    }
}
