package wooteco.subway.admin.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.dto.LineDetailResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.service.LineService;
import wooteco.subway.admin.service.LineStationService;

@RestController
public class LineStationController {

    private final LineStationService lineStationService;

    public LineStationController(LineStationService lineStationService) {
        this.lineStationService = lineStationService;
    }

    @GetMapping("/lines/detail")
    public ResponseEntity<List<LineDetailResponse>> getLineDetails() {
        List<LineDetailResponse> lines = lineStationService.findLinesWithStations();
        return ResponseEntity.ok(lines);
    }

    @GetMapping("/lines/{lineId}/detail")
    public ResponseEntity<LineDetailResponse> getStationResponse(@PathVariable Long lineId) {
        LineDetailResponse line = lineStationService.findLineWithStationsBy(lineId);
        return ResponseEntity.ok(line);
    }

    @PostMapping("/lines/{lineId}/stations")
    public ResponseEntity<Void> addLineStation(@PathVariable Long lineId,
        @RequestBody LineStationCreateRequest lineStationCreateRequest) {
        lineStationService.addStationInLine(lineId, lineStationCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/lines/{lineId}/stations/{stationId}")
    public ResponseEntity<Void> deleteLineStation(@PathVariable Long lineId,
        @PathVariable Long stationId) {
        lineStationService.removeStationFromLine(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
