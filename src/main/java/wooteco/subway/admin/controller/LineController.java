package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.service.LineService;

@RestController
@RequestMapping("/api/lines")
public class LineController {
    private final LineService service;

    public LineController(LineService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity addLine(@RequestBody LineRequest view) {
        Line line = view.toLine();
        Line persistLine = service.save(line);

        return ResponseEntity
            .created(URI.create("/api/lines/" + persistLine.getId()))
            .body(LineResponse.of(persistLine));
    }

    @GetMapping("")
    public ResponseEntity showLines() {
        return ResponseEntity.ok().body(service.showLines());
    }

    @GetMapping("/{id}")
    public ResponseEntity showStation(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.findLineWithStationsById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable Long id, @RequestBody LineRequest view) {
        service.updateLine(id, view.toLine());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        service.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    /* 구간 추가 */
    @PostMapping("/{lineId}/stations")
    public ResponseEntity addLineStation(@PathVariable Long lineId,
        @RequestBody LineStationCreateRequest view) {
        service.addLineStation(lineId, view);
        return ResponseEntity
            .created(URI.create("/api/lines/{lineId}/stations"))
            .build();
    }

    @GetMapping("/{lineId}/stations")
    public ResponseEntity getStations(@PathVariable Long lineId) {
        final LineResponse lineResponse = service.findLineWithStationsById(lineId);
        final Set<Station> stations = lineResponse.getStations();
        List<StationResponse> stationResponses = new ArrayList<>();
        for (Station station : stations) {
            stationResponses.add(StationResponse.of(station));
        }
        return ResponseEntity
            .ok()
            .body(stationResponses);
    }

    @DeleteMapping("/{lineId}/stations/{stationId}")
    public ResponseEntity removeLineStation(@PathVariable Long lineId,
        @PathVariable Long stationId) {
        service.removeLineStation(lineId, stationId);
        return ResponseEntity
            .noContent()
            .build();
    }
}
