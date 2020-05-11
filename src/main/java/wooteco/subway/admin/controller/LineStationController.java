package wooteco.subway.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.service.LineStationService;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
public class LineStationController {

    @Autowired
    private LineStationService lineStationService;

    @PostMapping("/lines/{lineId}/stations")
    public ResponseEntity<?> create(
            @PathVariable("lineId") Long lineId,
            @RequestBody LineStationCreateRequest request
    ) throws URISyntaxException {

        final Long preStationName = request.getPreStationId();
        final Long stationName = request.getStationId();
        final int distance = request.getDistance();
        final int duration = request.getDuration();

        LineStation lineStation = lineStationService.createLineStation(
                lineId, preStationName, stationName, distance, duration);

        final URI url = new URI("/lines/" + lineId + "/stations/" + lineStation.getStationId());
        return ResponseEntity.created(url)
                .body(lineStation);
    }

    @DeleteMapping("/lines/{lineId}/station/{id}")
    public ResponseEntity<?> delete(
            @PathVariable("lineId") Long lineId,
            @PathVariable("id") Long id
    ) {
        lineStationService.removeLineStation(lineId, id);
        return ResponseEntity.noContent().build();

    }
}
