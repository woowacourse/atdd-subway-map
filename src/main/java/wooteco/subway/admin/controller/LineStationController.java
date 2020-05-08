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
@RequestMapping("/line-stations")
public class LineStationController {
    private LineStationService lineStationService;

    public LineStationController(LineStationService lineStationService) {
        this.lineStationService = lineStationService;
    }

    @PostMapping
    public ResponseEntity createLineStation(@RequestBody Request<LineStationCreateRequest> lineStationRequest) {
        LineStation lineStation = lineStationRequest.getContent().toLineStationRequest();
        LineStation persistLineStation = lineStationService.save(lineStation);

        return ResponseEntity
                .created(URI.create("/line-stations/" + persistLineStation.getStationId()))
                .body(LineStationResponse.of(persistLineStation));
    }

    @GetMapping("/{lineId}")
    public ResponseEntity getLineStations(@PathVariable Long lineId) {
        List<LineStationResponse> lineStations = lineStationService.getLineStations(lineId);
        return ResponseEntity.ok().body(lineStations);
    }
}
