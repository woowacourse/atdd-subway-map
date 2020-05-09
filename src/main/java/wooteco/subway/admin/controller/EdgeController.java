package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.admin.service.LineService;

@RestController
public class EdgeController {
    private final LineService lineService;

    public EdgeController(LineService lineService) {
        this.lineService = lineService;
    }

    @GetMapping("/edge")
    public ResponseEntity showEdges() {
        return ResponseEntity.ok()
                .body(lineService.findAllLineStations());
    }
}
