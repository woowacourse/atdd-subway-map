package wooteco.subway.admin.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.dto.request.LineStationCreateRequest;
import wooteco.subway.admin.dto.resopnse.ApiError;
import wooteco.subway.admin.dto.resopnse.LineDetailResponse;
import wooteco.subway.admin.exception.LineStationException;
import wooteco.subway.admin.exception.NotFoundException;
import wooteco.subway.admin.service.LineStationService;

@RestController
public class LineStationController {

    private final LineStationService lineStationService;

    public LineStationController(LineStationService lineStationService) {
        this.lineStationService = lineStationService;
    }

    @GetMapping("/lines/detail")
    public ResponseEntity<List<LineDetailResponse>> getLineDetails() {
        List<LineDetailResponse> lines = lineStationService.findLinesWithStations();
        return ResponseEntity.ok(lines);
    }

    @GetMapping("/lines/{lineId}/detail")
    public ResponseEntity<LineDetailResponse> getStationResponse(@PathVariable Long lineId) {
        LineDetailResponse line = lineStationService.findLineWithStationsBy(lineId);
        return ResponseEntity.ok(line);
    }

    @PostMapping("/lines/{lineId}/stations")
    public ResponseEntity<Void> addLineStation(@PathVariable Long lineId,
        @RequestBody LineStationCreateRequest lineStationCreateRequest) {
        lineStationService.addStationInLine(lineId, lineStationCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/lines/{lineId}/stations/{stationId}")
    public ResponseEntity<Void> deleteLineStation(@PathVariable Long lineId,
        @PathVariable Long stationId) {
        lineStationService.removeStationFromLine(lineId, stationId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError notFoundException(NotFoundException exception) {
        return new ApiError(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(LineStationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError lineStationException(LineStationException exception) {
        return new ApiError(HttpStatus.BAD_REQUEST, exception.getMessage());
    }
}
