package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.domain.line.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.service.LineService;

@RestController
public class LineController {
    private LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity<String> createLine(@RequestBody @Valid LineRequest request, Errors errors) {
        if (errors.hasErrors()) {
            String message = Objects.requireNonNull(errors.getFieldError()).getDefaultMessage();
            return ResponseEntity.badRequest().body(message);
        }
        Line line = request.toLine();
        Long lineId = lineService.save(line);

        return ResponseEntity
            .created(URI.create("/lines/" + lineId))
            .build();
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> getLines() {
        List<LineResponse> lineResponses = lineService.findAllLineWithStations();
        return ResponseEntity.ok(lineResponses);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> getLine(@PathVariable Long id) {
        LineResponse line = lineService.findLineWithStationsById(id);
        return ResponseEntity.ok(line);
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<String> updateLine(@PathVariable Long id,
        @RequestBody @Valid LineRequest request, Errors errors) {
        if (errors.hasErrors()) {
            String message = Objects.requireNonNull(errors.getFieldError()).getDefaultMessage();
            return ResponseEntity.badRequest().body(message);
        }

        lineService.updateLine(id, request.toLine());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lines/{id}/stations")
    public ResponseEntity<Void> appendStationToLine(@PathVariable Long id,
        @RequestBody LineStationCreateRequest request, Errors errors) {
        lineService.addLineStation(id, request);
        return ResponseEntity.created(
            URI.create("/lines/" + id + "/stations/" + request.getStationId())).build();
    }

    @DeleteMapping("/lines/{lineId}/stations/{stationId}")
    public ResponseEntity<Void> excludeStationFromLine(@PathVariable Long lineId,
        @PathVariable Long stationId) {
        lineService.removeLineStation(lineId, stationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/lines/{id}/stations")
    public ResponseEntity<List<StationResponse>> getStations(@PathVariable Long id) {
        LineResponse response = lineService.findLineWithStationsById(id);
        return ResponseEntity.ok(response.getStations());
    }
}
