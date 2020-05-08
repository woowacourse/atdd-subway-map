package wooteco.subway.admin.controller;

import java.net.URI;
import java.time.LocalTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.LineStationResponse;
import wooteco.subway.admin.service.LineService;

@RestController
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
        Line line = lineRequest.toLine();

        if (lineService.existsByName(line.getName())) {
            return ResponseEntity
                .badRequest()
                .build();
        }

        Line persistLine = lineService.save(line);

        return ResponseEntity
            .created(URI.create("/lines/" + persistLine.getId()))
            .body(LineResponse.of(persistLine));
    }

    @GetMapping("/lines")
    public ResponseEntity showLines() {
        List<Line> persistLines = lineService.showLines();

        return ResponseEntity
            .ok()
            .body(LineResponse.listOf(persistLines));
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity showLine(@PathVariable Long id) {
        Line persistLine = lineService.showLine(id);

        return ResponseEntity
            .ok()
            .body(LineResponse.of(persistLine));
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        Line line = lineService.showLine(id);
        line.update(lineRequest.toLine());
        lineService.updateLine(id, line);

        return ResponseEntity
            .ok()
            .body(LineResponse.of(line));
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/line-station/{id}") // TODO: 2020-05-08 http 요청 메서드과 uri가 적절한지
    public ResponseEntity addLineStation(@PathVariable Long id, @RequestBody
        LineStationCreateRequest lineStationCreateRequest) {
        // TODO: 2020-05-08 원래는 서비스에서 호출해야 함
        LineStation lineStation = lineStationCreateRequest.toLineStation();

        return ResponseEntity
            .ok()
            .body(LineStationResponse.of(lineStation));
    }

    @GetMapping("/line-station/{id}")
    public ResponseEntity findLineWithStations(@PathVariable Long id) {
        // TODO: 2020-05-08 showLine으로 대체가능한지 확인하자!!

        Line line = new Line("잠실역", LocalTime.of(5, 30),
            LocalTime.of(5, 30), 10, "WHITE");
        LineStation lineStation = new LineStation(1L, 1L, 1, 1);
        line.addLineStation(lineStation);

        return ResponseEntity
            .ok()
            .body(LineResponse.of(line));
    }
}
