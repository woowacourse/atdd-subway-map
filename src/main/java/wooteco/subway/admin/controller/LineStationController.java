package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.service.LineService;

import java.util.List;

@RestController
public class LineStationController {
    private final LineService lineService;

    public LineStationController(final LineService lineService) {
        this.lineService = lineService;
    }

    @GetMapping("/lineStations/{lineId}")
    public ResponseEntity<List<LineStation>> findAllLineStations(@PathVariable Long lineId) {
        return ResponseEntity.ok(lineService.findLineStationByLineId(lineId));
    }
}
