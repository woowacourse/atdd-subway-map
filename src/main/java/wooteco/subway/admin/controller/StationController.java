package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.service.LineService;

import java.net.URI;

@RestController
public class StationController {
    private final LineService lineService;

    public StationController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/stations")
    public ResponseEntity createStation(@RequestBody StationCreateRequest view) {
        Station persistStation = lineService.save(view.toStation());
        return ResponseEntity
                .created(URI.create("/stations/" + persistStation.getId()))
                .body(StationResponse.of(persistStation));
    }

    @PostMapping("/lines")
    public ResponseEntity createLine(@RequestBody LineRequest view) {
        System.out.println(view.toString());
        Line persistLine = lineService.save(view.toLine());

        return ResponseEntity
                .created(URI.create("/lines" + persistLine.getId()))
                .body(LineResponse.of(persistLine));
    }

    @GetMapping("/stations")
    public ResponseEntity showStations() {
        return ResponseEntity.ok().body(lineService.showStations());
    }

    @GetMapping("/lines")
    public ResponseEntity showLines() {
        return ResponseEntity.ok(lineService.showLines());
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity showLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(LineResponse.of(lineService.findById(id)));
    }

    @PutMapping("/lines/{id}")
    public void updateLine(@PathVariable Long id, @RequestBody LineRequest view) {
        lineService.updateLine(id, view.toLine());
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity deleteStation(@PathVariable Long id) {
        lineService.deleteStationById(id);
        return ResponseEntity.noContent().build();
    }
}
