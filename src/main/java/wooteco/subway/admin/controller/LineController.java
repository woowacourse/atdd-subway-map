package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.service.LineService;
import wooteco.subway.admin.service.StationService;

import java.net.URI;
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
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest view) {
        Line line = view.toLine();
        Line persistLine = lineService.save(line);

        return ResponseEntity
                .created(URI.create("/lines/" + persistLine.getId()))
                .body(LineResponse.of(persistLine));
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> getLine(@PathVariable Long id) {
        Line line = lineService.findLineWithStationsById(id);
        LineResponse lineResponse = LineResponse.of(line);
        List<Long> lineStationsId = line.getLineStationsId();

        for (long stationId : lineStationsId){
            lineResponse.addStation(stationService.findById(stationId));
        }

        return ResponseEntity.ok()
                .body(lineResponse);
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineRequest view) {
        Line persistLine = lineService.updateLine(id, view.toLine());
        return ResponseEntity.ok()
                .body(LineResponse.of(persistLine));
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> getLines() {
        List<Line> lines = lineService.showLines();
        System.out.println(lines);
        return ResponseEntity.ok()
                .body(LineResponse.listOf(lines));
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<LineResponse> deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent()
                .build();
    }

    @PutMapping("/lines/{id}/stations")
    public ResponseEntity<LineResponse> createLineStation(@PathVariable Long id,
                                            @RequestBody LineStationCreateRequest lineStationCreateRequest){
        Line persistLine = lineService.addLineStation(id, lineStationCreateRequest.toLineStation());

        return ResponseEntity.ok()
                .body(LineResponse.of(persistLine));
    }

    @DeleteMapping("/lines/{lineId}/stations/{stationId}")
    public void deleteLineStation(@PathVariable Long lineId, @PathVariable Long stationId){
        Line line = lineService.findLineWithStationsById(lineId);

        line.removeLineStationById(stationId);
        System.out.println(line.getStations().size());
        lineService.updateLine(lineId, line);
    }
}