package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationDto;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.service.LineService;

import java.net.URI;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
        Line line = lineRequest.toLine();
        Line persistLine = lineService.save(line);
        return ResponseEntity
                .created(URI.create("/lines/" + persistLine.getId()))
                .body(LineResponse.of(line));
    }

    @PostMapping("/lines/line-stations")
    public ResponseEntity registerLineStation(@RequestBody LineStationDto lineStationDto) {
        LineResponse lineResponse = lineService.registerLineStation(lineStationDto);
        return ResponseEntity
                .created(URI.create("/lines/" + lineResponse.getId()))
                .body(lineResponse);
    }

    @GetMapping("/lines")
    public ResponseEntity showLines() {
        List<Line> lines = lineService.showLines();
        List<LineResponse> lineResponses = new ArrayList<>();
        for (Line line : lines) {
            LineResponse lineResponse = lineService.findLineWithStationsById(line.getId());
            lineResponses.add(lineResponse);
        }
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity showLine(@PathVariable Long id) {
        try {
            LineResponse lineResponse = lineService.findLineWithStationsById(id);
            return ResponseEntity.ok().body(lineResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/lines/{id}/stations")
    public ResponseEntity showStationsOfLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(LineResponse.of(new Line()));
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        Line line = lineRequest.toLine();
        lineService.updateLine(id, line);
        return ResponseEntity.ok().body(LineResponse.of(line));
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        Line line = lineService.findById(id);
        lineService.delete(line);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lines/{id}/register")
    public ResponseEntity registerStation(@PathVariable Long id,
                                          @RequestBody StationCreateRequest stationCreateRequest) {
        Line line = new Line("2호선", LocalTime.now(), LocalTime.now(), 3, "red");
        LineStation lineStation = new LineStation(1L, 1L, 1, 1);
        line.addLineStation(lineStation);

        return ResponseEntity
                .created(URI.create("/lines/" + id))
                .body(LineResponse.of(line));
    }

    @DeleteMapping("/lines/{lineId}/line-stations/{stationId}")
    public ResponseEntity deleteLineStation(@PathVariable Long lineId, @PathVariable Long stationId) {
        Line line = lineService.findById(lineId);
        line.removeLineStationById(stationId);
        lineService.save(line);
        return ResponseEntity.noContent().build();
    }
}
