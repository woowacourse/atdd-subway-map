package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.LineStationResponse;

import java.net.URI;

@RestController
public class LineStationController {
    @PostMapping("/lines/{id}/stations")
    public ResponseEntity create(@RequestBody LineStationCreateRequest lineStationCreateRequest, @PathVariable String id) {
        return ResponseEntity
                .created(URI.create("/line-station/" + id + "/stations"))
                .body(new LineStationResponse(1L, 2L, 3, 4));
    }
}
