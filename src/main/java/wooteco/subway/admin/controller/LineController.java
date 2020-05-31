package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.*;
import wooteco.subway.admin.service.LineService;
import wooteco.subway.admin.service.StationService;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
public class LineController {
    private final LineService lineService;
    private final StationService stationService;

    public LineController(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = lineRequest.toLine();
        line = lineService.save(line);
        return ResponseEntity
                .created(URI.create("/lines/" + line.getId()))
                .body(LineResponse.withoutStations(line));
    }

    @Transactional
    @PostMapping("/lines/{id}/stations")
    //todo: change rdoto
    public ResponseEntity<LineResponse> registerLineStation(@PathVariable Long id, @RequestBody LineStationCreateRequest lineStationCreateRequest) {
        Line line = lineService.addLineStation(id, lineStationCreateRequest);
        Set<Station> stations = stationService.findAllOf(line);
        LineResponse lineResponse = LineResponse.withStations(line, stations);
        return ResponseEntity
                .created(URI.create("/lines/" + lineResponse.getId()))
                .body(lineResponse);
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResponses = new ArrayList<>();
        List<Line> lines = lineService.getLines();
        for (Line line : lines) {
            Set<Station> stations = stationService.findAllOf(line);
            LineResponse lineResponse = LineResponse.withStations(line, stations);
            lineResponses.add(lineResponse);
        }

        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line line = lineService.findById(id);
        return ResponseEntity.ok().body(LineResponse.withoutStations(line));
    }

    @GetMapping("/lines/{id}/stations")
    public ResponseEntity<List<StationResponse>> showStationsOfLine(@PathVariable Long id) {
        Line line = lineService.findById(id);
        Set<Station> stations = stationService.findAllOf(line);
        List<StationResponse> stationResponses = new ArrayList<>();
        for (Station station : stations) {
            StationResponse stationResponse = new StationResponse(station.getId(), station.getName(), station.getCreatedAt());
            stationResponses.add(stationResponse);
        }
        return ResponseEntity.ok().body(stationResponses);
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        Line line = lineRequest.toLine();
        lineService.updateLine(id, line);
        return ResponseEntity.ok().body(LineResponse.withoutStations(line));
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<LineResponse> deleteLine(@PathVariable Long id) {
        Line line = lineService.findById(id);
        lineService.delete(line);
        return ResponseEntity.noContent().build();
    }
}
