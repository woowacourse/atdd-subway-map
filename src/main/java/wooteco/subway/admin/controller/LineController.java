package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
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
import wooteco.subway.admin.service.LineService;

@RestController
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @GetMapping("/lines/{id}/stations")
    public ResponseEntity getStations(@PathVariable Long id) {
        Line line = lineService.findById(id);
        return ResponseEntity.ok().body(line.getStations());
    }

    @PostMapping("/lines/{id}/station-add")
    public ResponseEntity addStation(@PathVariable Long id, @RequestBody LineStationCreateRequest lineStationCreateRequest) {
        LineStation lineStation = new LineStation(1L, 1L, 1, 1);
        Line line = lineService.findById(id);
        line.addLineStation(lineStation);
        lineService.save(line);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/lines")
    public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
        if(lineService.contains(lineRequest.getName())) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        Line persistLine = lineService.save(lineRequest.toLine());

        return ResponseEntity.created(URI.create("/lines/" + persistLine.getId()))
            .body(LineResponse.of(persistLine));
    }

    @GetMapping("/lines")
    public List<LineResponse> getLines() {
        return LineResponse.listOf(lineService.showLines());
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity getLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(LineResponse.of(lineService.findById(id)));
    }


    @PutMapping("/lines/{id}")
    public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        Line line = lineService.updateLine(id, lineRequest.toLine());
        return ResponseEntity.ok().body(LineResponse.of(line));
    }

    @DeleteMapping("/lines/{id}")
    public void deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
    }
}
