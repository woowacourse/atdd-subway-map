package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    private List<LineStationResponse> response;

    public LineStationController(LineService lineService) {
        this.response = new ArrayList<>();
    }

    @PostMapping("/{lineId}")
    public ResponseEntity create(@PathVariable Long lineId,
        @RequestBody LineStationCreateRequest request) {
        final LineStationResponse response =
            new LineStationResponse(1L, new StationResponse(), new StationResponse(), 1, 2,
                new LineResponse());
        this.response.add(response);
        return ResponseEntity.created(URI.create("/edges/" + 2))
            .body(response);
    }

    @GetMapping
    public ResponseEntity showAll() {
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteById(@PathVariable Long id) {
        response.remove(0);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
