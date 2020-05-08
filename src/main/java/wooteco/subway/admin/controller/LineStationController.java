package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.LineStationResponse;
import wooteco.subway.admin.dto.Request;
import wooteco.subway.admin.service.LineStationService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines/{lineId}/stations")
public class LineStationController {
    private final LineStationService lineStationService;

    public LineStationController(LineStationService lineStationService) {
        this.lineStationService = lineStationService;
    }

    @PostMapping
    public ResponseEntity createLineStation(@PathVariable Long lineId, @RequestBody Request<LineStationCreateRequest> lineStationRequest) {
        LineStation lineStation = lineStationRequest.getContent().toLineStationRequest();
        LineStation persistLineStation = lineStationService.save(lineStation);

        return ResponseEntity
                .created(URI.create("/lines/" + lineId + "/stations/" + persistLineStation.getStationId()))
                .body(LineStationResponse.of(persistLineStation));
    }

    @GetMapping
    public ResponseEntity getLineStations(@PathVariable Long lineId) {
        List<LineStationResponse> lineStations = lineStationService.getLineStations(lineId);
        return ResponseEntity.ok().body(lineStations);
    }

    @DeleteMapping("/{stationId}")
    public ResponseEntity deleteLine(@PathVariable Long lineId, @PathVariable Long stationId) {
        lineStationService.deleteLineStationById(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
