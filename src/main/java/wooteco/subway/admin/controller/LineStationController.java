package wooteco.subway.admin.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.dto.req.LineStationCreateRequest;
import wooteco.subway.admin.dto.res.LineResponse;
import wooteco.subway.admin.service.LineService;

@RestController
@RequestMapping("/edges")
public class LineStationController {
    private final LineService lineService;

    public LineStationController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/{lineId}")
    public ResponseEntity<LineResponse> create(@PathVariable Long lineId,
        @RequestBody LineStationCreateRequest request) {
        LineResponse lineResponse = lineService.addLineStation(lineId, request);
        return ResponseEntity
            .created(URI.create("/edges/" + lineId))
            .body(lineResponse);
    }

    @DeleteMapping("/{lineId}/{stationId}")
    public ResponseEntity<Void> deleteById(@PathVariable("lineId") Long lineId,
        @PathVariable("stationId") Long stationId) {
        lineService.removeLineStation(lineId, stationId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
