package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.dto.LineIdResponse;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.service.LineService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineIdResponse> save(@Valid @RequestBody LineRequest lineRequest) {
        Long id = lineService.save(lineRequest.toLine()).getId();
        return ResponseEntity
                .created(URI.create("/lines/" + id))
                .body(new LineIdResponse(id));
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok()
                .body(LineResponse.listOf(lineService.showLines()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> findLineById(@PathVariable Long id) {
        return ResponseEntity.ok()
                .body(LineResponse.of(lineService.findLineById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.updateLine(id, lineRequest.toLine());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLineById(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/stations")
    public ResponseEntity<Void> addLineStation(@PathVariable Long id, @Valid @RequestBody LineStationCreateRequest lineStationCreateRequest) {
        lineService.addLineStation(id, lineStationCreateRequest.toLineStation());
        return ResponseEntity
                .created(URI.create("/lines/" + id + "/stations"))
                .build();
    }

    @DeleteMapping("/{lineId}/stations/{stationId}")
    public ResponseEntity<Void> removeLineStation(@PathVariable Long lineId, @PathVariable Long stationId) {
        lineService.removeLineStation(lineId, stationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/stations")
    public ResponseEntity<LineResponse> findLineWithStationsById(@PathVariable Long id) {
        return ResponseEntity.ok()
                .body(lineService.findLineWithStationsById(id));
    }

    @GetMapping("/stations")
    public ResponseEntity<List<LineResponse>> findAllLineWithStations() {
        return ResponseEntity.ok()
                .body(lineService.findAllLineWithStations());
    }
}
