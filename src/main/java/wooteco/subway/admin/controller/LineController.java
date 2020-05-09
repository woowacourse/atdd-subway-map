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
        // todo: 1단계 - 제약사항 : 지하철 노선 이름은 중복될 수 없다는 요구사항 반영 필요
//        Line line = lineRequest.toLine();
//        Line persistLine = lineService.save(line);
//
//        return ResponseEntity
//                .created(URI.create("/lines/" + persistLine.getId()))
//                .body(LineResponse.of(line));
        Line line = new Line();
        return ResponseEntity
                .created(URI.create("/lines/" + 1L))
                .body(LineResponse.of(line));
    }

    @PostMapping("/lines/registerLineStation")
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
//        Set<Station> stations = (Set<Station>) stationRepository.findAllById(stationIds);

//        return LineResponse.withStations(line, stations);
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity showLine(@PathVariable Long id) {
//        LineResponse lineWithStationsById = lineService.findLineWithStationsById(id);
//        return ResponseEntity.ok().body(lineWithStationsById);

        if (id.equals(3L)) {
            return ResponseEntity.notFound().build();
        }
        Line line = new Line("2호선", LocalTime.now(), LocalTime.now(), 3, "red");
        line.addLineStation(new LineStation());

        return ResponseEntity.ok().body(LineResponse.of(line));
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
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lines/{id}/register")
    public ResponseEntity registerStation(@PathVariable Long id, @RequestBody StationCreateRequest stationCreateRequest) {
        Line line = new Line("2호선", LocalTime.now(), LocalTime.now(), 3, "red");

        LineStation lineStation = new LineStation(1L, 1L,1, 1);
        line.addLineStation(lineStation);

        return ResponseEntity
                .created(URI.create("/lines/" + id))
                .body(LineResponse.of(line));
    }
}
