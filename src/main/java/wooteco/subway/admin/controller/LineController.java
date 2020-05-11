package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        Set<Station> stations = lineService.toStations(persistLine.findLineStationsId());

        return ResponseEntity
            .created(URI.create("/lines/" + persistLine.getId()))
            .body(LineResponse.of(persistLine, stations));
    }

    @GetMapping("/lines")
    public ResponseEntity showLines() {
        List<Line> persistLines = lineService.showLines();
        Map<Line, Set<Station>> lineWithStations = persistLines.stream()
            .collect(Collectors.toMap(Function.identity(),
                persistLine -> lineService.toStations(persistLine.findLineStationsId())));

        return ResponseEntity
            .ok()
            .body(LineResponse.listOf(lineWithStations));
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity showLine(@PathVariable Long id) {
        Line persistLine = lineService.showLine(id);
        Set<Station> stations = lineService.toStations(persistLine.findLineStationsId());

        return ResponseEntity
            .ok()
            .body(LineResponse.of(persistLine, stations));
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        Line line = lineService.showLine(id);
        line.update(lineRequest.toLine());
        lineService.updateLine(id, line);
        Set<Station> stations = lineService.toStations(line.findLineStationsId());


        return ResponseEntity
            .ok()
            .body(LineResponse.of(line, stations));
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/line/{id}/stations") // TODO: 2020-05-08 http 요청 메서드과 uri가 적절한지
    public ResponseEntity addLineStation(@PathVariable Long id, @RequestBody
        LineStationCreateRequest lineStationCreateRequest) {
        lineService.addLineStation(id, lineStationCreateRequest);

        return ResponseEntity
            .ok()
            .body(LineStationResponse.of(lineStationCreateRequest.toLineStation()));
    }

    @DeleteMapping("/line/{lineId}/station/{stationId}")
    public ResponseEntity deleteLineStation(@PathVariable Long lineId,
        @PathVariable Long stationId) {
        lineService.removeLineStation(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
