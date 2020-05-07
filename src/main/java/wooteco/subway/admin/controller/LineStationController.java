package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.dto.req.LineStationCreateRequest;
import wooteco.subway.admin.dto.res.LineResponse;
import wooteco.subway.admin.dto.res.LineStationResponse;
import wooteco.subway.admin.dto.res.StationResponse;
import wooteco.subway.admin.service.LineService;

@RestController
@RequestMapping("/edges")
public class LineStationController {
    private final LineService lineService;

    public LineStationController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/{lineId}")
    public ResponseEntity create(@PathVariable Long lineId,
        @RequestBody LineStationCreateRequest request) {
        final LineStationResponse response =
            new LineStationResponse(new StationResponse(), new StationResponse(), 1, 2,
                new LineResponse());
        return ResponseEntity.created(URI.create("/edges/" + 2))
            .body(response);
    }

    @GetMapping
    public ResponseEntity showAll() {
        final List<LineStationResponse> response = Arrays.asList(
            new LineStationResponse(new StationResponse(), new StationResponse(), 1, 2,
                new LineResponse()));
        return ResponseEntity.ok(response);
    }
}
