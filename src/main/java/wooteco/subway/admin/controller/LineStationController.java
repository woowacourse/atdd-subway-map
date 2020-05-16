package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.dto.LineStationRequest;
import wooteco.subway.admin.service.LineService;
import wooteco.subway.admin.service.StationService;

// TODO : addLineStationService의 preStationId 찾는 로직을 모두 Service로 옮겨버리자!
@RestController
public class LineStationController {

    private final LineService lineService;
    private final StationService stationService;

    public LineStationController(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    @PostMapping("/line-stations")
    public ResponseEntity<Void> createLineStation(@RequestBody LineStationRequest view) {
        lineService.addLineStation(view);

        return ResponseEntity
                .ok()
                .build();
    }

    @DeleteMapping("/line-stations/{lineId}/{stationId}")
    public ResponseEntity<Void> deleteLineStation(@PathVariable("lineId") Long lineId, @PathVariable("stationId") Long stationId) {
        lineService.removeLineStation(lineId, stationId);

        return ResponseEntity
                .ok()
                .build();
    }
}
