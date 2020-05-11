package wooteco.subway.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.LineStationRequest;
import wooteco.subway.admin.dto.LineStationResponse;
import wooteco.subway.admin.exception.WrongIdException;
import wooteco.subway.admin.exception.WrongNameException;
import wooteco.subway.admin.repository.StationRepository;
import wooteco.subway.admin.service.LineService;

import java.net.URI;

@RestController
@RequestMapping("/lineStations")
public class LineStationController {

    public static final int DEFAULT_DISTANCE = 0;
    public static final int DEFAULT_DURATION = 0;

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

        LineStationRequest requestWithId = new LineStationRequest(request.getLineId(), preStationId, stationId, DEFAULT_DISTANCE, DEFAULT_DURATION);
        LineStationResponse lineStationResponse = lineService.addLineStation(requestWithId);

        return ResponseEntity.created(
                URI.create("/lineStations/" + request.getLineId() + "/" + stationId))
                .body(lineStationResponse);
    }

    @DeleteMapping("/{lineId}/stations/{stationId}")
    public ResponseEntity deleteLineStation(@PathVariable Long lineId,
                                            @PathVariable Long stationId) {
        lineService.removeLineStation(lineId, stationId);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler({WrongIdException.class, WrongNameException.class})
    public ResponseEntity exceptionHandler(Errors errors) {
        return ResponseEntity.badRequest().body(errors);
    }
}
