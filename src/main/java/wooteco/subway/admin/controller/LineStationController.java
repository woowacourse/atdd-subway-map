package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.LineStationResponse;

import java.net.URI;
import java.util.Arrays;

@RestController
public class LineStationController {
    @PostMapping("/lines/{lineId}/stations")
    public ResponseEntity createLineStation(@RequestBody LineStationCreateRequest lineStationCreateRequest, @PathVariable String lineId) {
        return ResponseEntity
                .created(URI.create("/line-station/" + lineId + "/stations/" + lineStationCreateRequest.getStationId()))
                .body(Arrays.asList(new LineStationResponse(1L, 2L, 3, 4)));
    }

    @GetMapping("/lines/{lineId}/stations")
    public ResponseEntity showLineStations(@PathVariable String lineId) {
        return ResponseEntity
                .ok()
                .body(Arrays.asList(new LineStationResponse(1L, 2L, 3, 4)));
    }

    @DeleteMapping("/lines/{id}/stations/{stationId}")
    public ResponseEntity deleteLineStation(@PathVariable String lineId, @PathVariable String StationId) {
        return ResponseEntity
                .noContent()
                .build();
    }
}
