package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.LineStationResponse;

@RestController
@RequestMapping("/lineStations")
public class LineStationController {

    private static List<LineStationResponse> mockResponses = new ArrayList<>();
    private static int id = 1;

    @GetMapping
    public ResponseEntity getLineStations() {
        return ResponseEntity.ok(mockResponses);
    }

    @PostMapping
    public ResponseEntity createLineStation(@RequestBody LineStationCreateRequest request) {

        LineStationResponse lineStationResponse = new LineStationResponse(1L, request.getPreStationId(), request.getStationId(), request.getDistance(), request.getDuration());
        mockResponses.add(lineStationResponse);

        return ResponseEntity.created(URI.create("/lineStation/"+(id++)))
            .body(lineStationResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLineStation(@PathVariable Long id) {
        mockResponses.remove(0);
        return ResponseEntity.ok().build();
    }
}
