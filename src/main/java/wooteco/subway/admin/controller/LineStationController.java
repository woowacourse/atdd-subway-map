package wooteco.subway.admin.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.LineStationRequest;
import wooteco.subway.admin.dto.LineStationResponse;
import wooteco.subway.admin.repository.StationRepository;
import wooteco.subway.admin.service.LineService;

@RestController
@RequestMapping("/lineStations")
public class LineStationController {

    @Autowired
    private LineService lineService;

    @Autowired
    private StationRepository stationRepository;

    @GetMapping
    public ResponseEntity getLineStations() {
        return ResponseEntity.ok(lineService.showLines());
    }

    @PostMapping
    public ResponseEntity createLineStation(@RequestBody LineStationCreateRequest request) {
        Long preStationId = stationRepository.findIdByName(request.getPreStationName());
        Long stationId = stationRepository.findIdByName(request.getStationName());

        LineStationRequest requestWithId = new LineStationRequest(request.getLineId(), preStationId,
            stationId, 0, 0);
        LineStationResponse lineStationResponse = lineService.addLineStation(requestWithId);

        return ResponseEntity.created(
            URI.create("/lineStations/" + request.getLineId() + "/" + stationId))
            .body(lineStationResponse);
    }

    @DeleteMapping("/delete/{lineId}/{stationId}")
    public ResponseEntity deleteLineStation(@PathVariable Long lineId,
        @PathVariable Long stationId) {
        lineService.removeLineStation(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
