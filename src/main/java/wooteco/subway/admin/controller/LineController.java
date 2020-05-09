package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.service.LineService;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @GetMapping()
    public List<LineResponse> getLines() {
        List<Line> lines = lineService.showLines();

        return lines.stream()
                .map(line -> lineService.findLineWithStationsById(line.getId()))
                .collect(Collectors.toList());
    }

    @PostMapping()
    public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
        if(lineService.contains(lineRequest.getName())) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        Line persistLine = lineService.save(lineRequest.toLine());

        return ResponseEntity.created(URI.create("/lines/" + persistLine.getId()))
                .body(lineService.findLineWithStationsById(persistLine.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity getLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(lineService.findLineWithStationsById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        Line line = lineService.updateLine(id, lineRequest.toLine());
        return ResponseEntity.ok().body(lineService.findLineWithStationsById(line.getId()));
    }

    @DeleteMapping("/{id}")
    public void deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
    }

    @GetMapping("/{id}/stations")
    public ResponseEntity getLineStations(@PathVariable Long id) {
        Line line = lineService.findById(id);
        return ResponseEntity.ok().body(line.getStations());
    }

    @PostMapping("/{id}/stations")
    public ResponseEntity createLineStation(@PathVariable Long id, @RequestBody LineStationCreateRequest lineStationCreateRequest) {
        lineService.addLineStation(id, lineStationCreateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineId}/stations/{stationId}")
    public void deleteLineStation(@PathVariable Long lineId, @PathVariable Long stationId) {
        lineService.removeLineStation(lineId, stationId);
    }
}
