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

import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.LineStationRequest;
import wooteco.subway.admin.dto.LineStationResponse;
import wooteco.subway.admin.service.LineService;
import wooteco.subway.admin.service.StationService;

@RestController
@RequestMapping("/lines")
public class LineController {

    private static final int DEFAULT_DISTANCE = 0;
    private static final int DEFAULT_DURATION = 0;
    private final LineService lineService;
    private final StationService stationService;

    public LineController(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@Valid @RequestBody LineRequest request) {
        LineResponse lineResponse = lineService.save(request);

        return ResponseEntity
            .created(URI.create("/lines/" + lineResponse.getId()))
            .body(lineResponse);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> getLines() {
        List<LineResponse> lines = lineService.findAll();

        return ResponseEntity
            .ok()
            .body(lines);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> getLine(@PathVariable Long id) {
        LineResponse lineResponse = lineService.findLineWithStationsById(id);

        return ResponseEntity
            .ok(lineResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLine(@Valid @PathVariable Long id,
        @RequestBody LineRequest request) {
        lineService.updateLine(id, request);

        return ResponseEntity
            .ok()
            .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);

        return ResponseEntity
            .noContent()
            .build();
    }

    @PostMapping("/{id}")
    public ResponseEntity<LineStationResponse> createLineStation(
        @PathVariable Long id, @Valid @RequestBody LineStationCreateRequest request) {
        Long preStationId = stationService.findIdByName(request.getPreStationName());
        Long stationId = stationService.findIdByName(request.getStationName());

        LineStationRequest requestWithId = new LineStationRequest(id, preStationId,
            stationId, DEFAULT_DISTANCE, DEFAULT_DURATION);
        LineStationResponse lineStationResponse = lineService.addLineStation(requestWithId);

        return ResponseEntity.created(
            URI.create("/lines/" + id + "/stations/" + stationId))
            .body(lineStationResponse);
    }

    @DeleteMapping("/{lineId}/stations/{stationId}")
    public ResponseEntity<Void> deleteLineStation(@PathVariable Long lineId,
        @PathVariable Long stationId) {
        lineService.deleteLineStation(lineId, stationId);
        return ResponseEntity
            .noContent()
            .build();
    }
}
