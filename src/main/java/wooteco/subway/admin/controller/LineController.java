package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.service.LineService;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @GetMapping
    public List<LineResponse> showLines() {
        return lineService.findAllLines();
    }

    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody LineRequest lineRequest
    ) throws URISyntaxException {

        final LineResponse response = lineService.saveLine(lineRequest.toLine());
        final URI url = new URI("/lines/" + response.getId());
        return ResponseEntity.created(url).body("{}");
    }

    @GetMapping("/{id}")
    public LineResponse showLine(
            @PathVariable("id") Long id
    ) {
        return lineService.getLineWithStationsById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody LineRequest lineRequest
    ) {
        lineService.updateLine(id, lineRequest.toLine());
        return ResponseEntity.ok("{}");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable("id") Long id
    ) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{lineId}/stations")
    public ResponseEntity<?> showLineWithStations(
            @PathVariable("lineId") Long lineId
    ) {
        LineResponse lineResponse = lineService.getLineWithStationsById(lineId);
        List<StationResponse> lineStations = new ArrayList<>(lineResponse.getStations());
        return ResponseEntity.ok().body(lineStations);
    }

    @PostMapping("/{lineId}/stations")
    public ResponseEntity<?> create(
            @PathVariable("lineId") Long lineId,
            @Valid @RequestBody LineStationCreateRequest request
    ) throws URISyntaxException {

        lineService.addLineStation(lineId, request);

        final URI url = new URI("/lines/" + lineId + "/stations/" + request.getStationId());
        return ResponseEntity.created(url).body("{}");
    }

    @DeleteMapping("/{lineId}/stations/{id}")
    public ResponseEntity<?> delete(
            @PathVariable("lineId") Long lineId,
            @PathVariable("id") Long id
    ) {
        lineService.removeLineStation(lineId, id);
        return ResponseEntity.noContent().build();
    }
}
