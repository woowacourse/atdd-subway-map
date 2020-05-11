package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
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
        try {
            Line line = lineRequest.toLine();
            line = lineService.save(line);
            return ResponseEntity
                    .created(URI.create("/lines/" + line.getId()))
                    .body(LineResponse.of(line));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .build();
        }
    }

    @PostMapping("/lines/stations")
    public ResponseEntity<LineResponse> registerLineStation(@RequestBody LineStationDto lineStationDto, @RequestParam String name) {
        try {
            Line line = lineService.findByName(name);
            Station preStation = stationService.findByName(lineStationDto.getPreStationName());
            Station arrivalStation = stationService.save(lineStationDto.getArrivalStationName());
            LineStationCreateRequest lineStationCreateRequest = new LineStationCreateRequest(preStation.getId(), arrivalStation.getId(), 10, 10);

            lineService.addLineStation(line.getId(), lineStationCreateRequest);
            Set<Station> stations = stationService.findAllOf(line);
            LineResponse lineResponse = LineResponse.withStations(line, stations);
            return ResponseEntity
                    .created(URI.create("/lines/" + lineResponse.getId()))
                    .body(lineResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .build();
        }

    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> showLines() {
        try {
            List<LineResponse> lineResponses = findLineWithStations();
            return ResponseEntity.ok().body(lineResponses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .build();
        }
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        try {
            Line line = lineService.findById(id);
            return ResponseEntity.ok().body(LineResponse.of(line));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }

    }

    @GetMapping("/lines/{id}/stations")
    public ResponseEntity<LineResponse> showStationsOfLine(@PathVariable Long id) {
        try {
            Line line = lineService.findById(id);
            return ResponseEntity.ok().body(LineResponse.of(line));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .build();
        }

    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        try {
            Line line = lineRequest.toLine();
            lineService.updateLine(id, line);
            return ResponseEntity.ok().body(LineResponse.of(line));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .build();
        }

    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<LineResponse> deleteLine(@PathVariable Long id) {
        try {
            Line line = lineService.findById(id);
            lineService.delete(line);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .build();
        }

    }

    @PostMapping("/lines/{id}/stations")
    public ResponseEntity<LineResponse> registerStation(@PathVariable Long id, @RequestBody StationCreateRequest stationCreateRequest) {
        try {
            Line line = lineService.findById(id);
            Station station = stationService.save(stationCreateRequest.getName());
            Station preStation = stationService.findByName(stationCreateRequest.getPreStationName());
            LineStation lineStation = new LineStation(preStation.getId(), station.getId(), stationCreateRequest.getDistance(), stationCreateRequest.getDuration());
            line.addLineStation(lineStation);
            return ResponseEntity
                    .created(URI.create("/lines/" + id))
                    .body(LineResponse.of(line));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .build();
        }

    }

    private List<LineResponse> findLineWithStations() {
        List<Line> lines = lineService.getLines();
        List<LineResponse> lineResponses = new ArrayList<>();
        for (Line line : lines) {
            LineResponse lineResponse = lineService.findLineWithStationsById(line.getId());
            lineResponses.add(lineResponse);
        }
        return lineResponses;
    }
}
