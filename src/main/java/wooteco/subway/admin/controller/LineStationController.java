package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.admin.dto.LineStationCreateRequest;

import java.net.URI;

@RestController
public class LineStationController {

    @PostMapping("/line-station")
    public ResponseEntity createLineStation(@RequestBody LineStationCreateRequest lineStationCreateRequest) {
        return ResponseEntity.created(URI.create("/line-station/")).build();
    }
}
