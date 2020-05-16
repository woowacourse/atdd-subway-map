package wooteco.subway.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.service.LineService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {

    @Autowired
    private LineService lineService;

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody LineRequest lineRequest
    ) throws URISyntaxException {

        Line created = lineService.save(lineRequest.toLine());

        final URI url = new URI("/lines/" + created.getId());
        return ResponseEntity.created(url).body("{}");
    }

    @GetMapping
    public List<LineResponse> lines() {
        return lineService.findAllLines();
    }

    @GetMapping("/{id}")
    public LineResponse line(
            @PathVariable("id") Long id
    ) {
        return LineResponse.of(lineService.findLineById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable("id") Long id,
            @RequestBody LineRequest lineRequest
    ) {
        Line line = lineRequest.toLine();
        lineService.updateLine(id, line);
        return ResponseEntity.ok(line);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable("id") Long id
    ) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{lineId}/stations")
    public ResponseEntity<?> stations(
            @PathVariable("lineId") Long lineId
    ) {
        LineResponse lineResponse = lineService.findLineWithStationsById(lineId);
        List<Station> lineStations = new ArrayList<>(lineResponse.getStations());
        return ResponseEntity.ok().body(lineStations);
    }

    @PostMapping("/{lineId}/stations")
    public ResponseEntity<?> create(
            @PathVariable("lineId") Long lineId,
            @RequestBody LineStationCreateRequest request
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
