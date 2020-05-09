package wooteco.subway.admin.controller.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationResponse;

@RestController
@RequestMapping("/api/lines")
public class MockController {

    private static Map<Long, Station> lineStationList = new HashMap<>();
    private static Map<Long, List<Long>> lineStation = new HashMap<>();
    //lineID / station의 ids

    @PostMapping("/{lineId}/stations/{stationId}")
    public ResponseEntity addLineStation(@PathVariable Long lineId, @PathVariable Long stationId) {
        lineStationList.put(stationId, new Station("오목교역"));
        final List<Long> orDefault = lineStation.getOrDefault(lineId, new ArrayList<>());
        orDefault.add(stationId);
        lineStation.put(lineId, orDefault);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }

    @GetMapping("/{lineId}/stations")
    public ResponseEntity getStations(@PathVariable Long lineId) {
        final List<StationResponse> collect = lineStation.get(lineId).stream()
            .map(id -> lineStationList.get(id))
            .map(StationResponse::of)
            .collect(Collectors.toList());
        return ResponseEntity
            .ok()
            .body(collect);
    }
}
