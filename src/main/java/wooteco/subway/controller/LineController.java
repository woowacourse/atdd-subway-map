package wooteco.subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.domain.line.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.service.SubwayService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final SubwayService subwayService;

    public LineController(final SubwayService subwayService) {
        this.subwayService = subwayService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = lineRequest.createLine();
        long id = subwayService.createLine(line);
        return ResponseEntity.created(URI.create("/lines/" + id)).body(new LineResponse(id, line));
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLine() {
        List<Line> lines = subwayService.showLines();
        List<LineResponse> lineResponses = lines.stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> showLineDetail(@PathVariable long id) {
        Line line = subwayService.showLineDetail(id);
        List<StationResponse> stationResponses = subwayService.getStationsInLine(id).stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(new LineResponse(line, stationResponses));
    }

    @PutMapping( "/{id}")
    public ResponseEntity<LineResponse> modifyLineDetail(@PathVariable long id,
                                                         @RequestBody LineRequest lineRequest) {
        subwayService.modifyLine(id, lineRequest.createLine());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable long id) {
        subwayService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }
}
