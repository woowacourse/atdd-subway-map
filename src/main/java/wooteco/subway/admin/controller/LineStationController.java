package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.LineStationResponse;
import wooteco.subway.admin.dto.Request;
import wooteco.subway.admin.service.LineService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines/{lineId}/stations")
public class LineStationController {
    private final LineService lineService;

    public LineStationController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity createLineStation(@PathVariable Long lineId,
                                            @RequestBody Request<LineStationCreateRequest> lineStationRequest) {
        LineStationCreateRequest lineStationCreateRequest = lineStationRequest.getContent();
        lineService.addLineStation(lineId, lineStationCreateRequest);

        return ResponseEntity
                .created(URI.create("/lines/" + lineId + "/stations/" + lineStationCreateRequest.getStationId()))
                .body(LineStationResponse.of(lineStationCreateRequest));
    }

    @GetMapping
    public ResponseEntity getLineStations(@PathVariable Long lineId) {
        List<LineStationResponse> lineStations = lineService.findLineStations(lineId);
        return ResponseEntity.ok().body(lineStations);
    }

    @DeleteMapping("/{stationId}")
    public ResponseEntity deleteLine(@PathVariable Long lineId, @PathVariable Long stationId) {
        lineService.removeLineStation(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
