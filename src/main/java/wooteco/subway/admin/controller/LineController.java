package wooteco.subway.admin.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.LineStationResponse;
import wooteco.subway.admin.service.LineService;

@RestController
public class LineController {

    private final LineService lineService;

    private final Set<Station> mockLineStations = new HashSet<>();

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity createLine(@RequestBody LineRequest view) {
        Line line = view.toLine();
        try {
            Line persistLine = lineService.save(line);
            return ResponseEntity
                .created(URI.create("/lines/" + persistLine.getId()))
                .body(LineResponse.of(persistLine));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/lines")
    public ResponseEntity showLines() {
        return ResponseEntity.ok().body(lineService.showLines());
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity showLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(lineService.showLine(id));
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest view) {
        Line line = lineService.updateLine(id, view.toLine());
        return ResponseEntity.ok().body(line);
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lines/{id}/stations")
    public ResponseEntity addLineStation(@PathVariable Long id,
        @RequestBody LineStationCreateRequest lineStationCreateRequest) {
        mockLineStations.add(new Station(lineStationCreateRequest.getStationId(), "잠실역"));

        lineService.addLineStation(id, lineStationCreateRequest);

        return ResponseEntity.created(URI.create("/line/" + id + "/stations"))
            .body(new LineStationResponse(1L, lineStationCreateRequest.getStationId(),
                lineStationCreateRequest.getPreStationId(), lineStationCreateRequest.getDistance(),
                lineStationCreateRequest.getDuration(), LocalDateTime.now(), LocalDateTime.now()));
    }

    @GetMapping("/lines/{id}/stations")
    public ResponseEntity findLine(@PathVariable Long id) {
        return ResponseEntity.ok()
            .body(new LineResponse(id, "2호선", LocalTime.now(), LocalTime.now(), 10,
                "bg-red-100", LocalDateTime.now(), LocalDateTime.now(), mockLineStations));
    }

    @DeleteMapping("/lines/{lineId}/stations/{stationId}")
    public ResponseEntity deleteLineStation(@PathVariable Long lineId,
        @PathVariable Long stationId) {
        mockLineStations.removeIf(station -> station.getId() == stationId);
        return ResponseEntity.noContent().build();
    }
}
