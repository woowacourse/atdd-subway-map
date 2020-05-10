package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.LineStationRequest;
import wooteco.subway.admin.dto.LineStationResponse;
import wooteco.subway.admin.service.LineService;
import wooteco.subway.admin.service.StationService;

@RestController
@RequestMapping("/lineStations")
public class LineStationController {

    public static final int DEFAULT_DISTANCE = 0;
    public static final int DEFAULT_DURATION = 0;

    private final LineService lineService;
    private final StationService stationService;

    public LineStationController(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> getLineStations() {
        return ResponseEntity
            .ok(lineService.findAll());
    }

    @PostMapping
    public ResponseEntity<LineStationResponse> createLineStation(
        @Valid @RequestBody LineStationCreateRequest request) {
        Long preStationId = stationService.findIdByName(request.getPreStationName());
        Long stationId = stationService.findIdByName(request.getStationName());

        LineStationRequest requestWithId = new LineStationRequest(request.getLineId(), preStationId,
            stationId, DEFAULT_DISTANCE, DEFAULT_DURATION);
        LineStationResponse lineStationResponse = lineService.addLineStation(requestWithId);

        return ResponseEntity.created(
            URI.create("/lineStations/" + request.getLineId() + "/" + stationId))
            .body(lineStationResponse);
    }

    @DeleteMapping("/lines/{lineId}/stations/{stationId}")
    public ResponseEntity<Object> deleteLineStation(@PathVariable Long lineId,
        @PathVariable Long stationId) {
        lineService.deleteLineStation(lineId, stationId);
        return ResponseEntity
            .noContent()
            .build();
    }
}
