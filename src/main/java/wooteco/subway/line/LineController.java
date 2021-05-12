package wooteco.subway.line;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.section.SectionRequest;
import wooteco.subway.section.SectionService;
import wooteco.subway.station.StationResponse;
import wooteco.subway.station.StationService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {

    private LineService lineService;
    private StationService stationService;

    public LineController(final LineService lineService, final StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> create(@RequestBody final LineRequest lineRequest) {
        final Line line = lineService.create(lineRequest.toLine());

        final LineResponse lineResponse = new LineResponse(line);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(lineResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> update(@PathVariable final Long id, @RequestBody final LineRequest lineRequest) {
        final Line line = new Line(id, lineRequest.getName(), lineRequest.getColor());
        lineService.update(line);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final Long id) {
        lineService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> lines() {
        final List<Line> lines = lineService.findAll();

        final List<LineResponse> lineResponses = lines.stream()
                .map(line -> new LineResponse(line, stationResponses(line.getId())))
                .collect(Collectors.toList());

        return ResponseEntity.ok(lineResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> line(@PathVariable final Long id) {
        final Line line = lineService.findById(id);

        final LineResponse lineResponse = new LineResponse(line, stationResponses(line.getId()));
        return ResponseEntity.ok(lineResponse);
    }

    private List<StationResponse> stationResponses(final Long lineId){
        final List<Long> stationIds = lineService.allStationIdInLine(lineId);
        return stationIds.stream()
                .map(stationId -> new StationResponse(stationService.findById(stationId)))
                .collect(Collectors.toList());
    }
}
