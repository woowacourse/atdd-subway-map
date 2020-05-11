package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.admin.service.LineService;

@RestController
public class LineStationController {
    private final LineService lineService;

    public LineStationController(final LineService lineService) {
        this.lineService = lineService;
    }

    @GetMapping("/lineStations/{lineId}")
    public ResponseEntity findAllLineStations(@PathVariable Long lineId) {
        return ResponseEntity.ok(lineService.findLineStationByLineId(lineId));
    }

    @ExceptionHandler
    public ResponseEntity exceptionHandler(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
