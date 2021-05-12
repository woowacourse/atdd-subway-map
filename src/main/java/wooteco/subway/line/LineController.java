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
    private SectionService sectionService;
    private StationService stationService;

    public LineController(final LineService lineService, final SectionService sectionService, final StationService stationService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> create(@RequestBody final LineRequest lineRequest) {
        final Line line = lineService.create(new Line(lineRequest.getName(), lineRequest.getColor(), lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance()));

        final LineResponse lineResponse = new LineResponse(line);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(lineResponse);
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity<Void> insertSection(@PathVariable final Long lineId, @RequestBody final SectionRequest sectionRequest){
        sectionService.addSection(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
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

    private List<StationResponse> stationResponses(final Long lineId){
        final List<Long> ids = lineService.allStationIdInLine(lineId);
        return ids.stream()
                .map(id -> new StationResponse(stationService.findById(id)))
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity<List<LineResponse>> deleteSection(@PathVariable final Long lineId, @RequestParam final Long stationId) {
        sectionService.deleteSection(lineId, stationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> line(@PathVariable final Long id) {
        final Line line = lineService.findById(id);

        final LineResponse lineResponse = new LineResponse(line, stationResponses(line.getId()));
        return ResponseEntity.ok(lineResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> update(@RequestBody final LineRequest lineRequest, @PathVariable final Long id) {
        lineService.update(new Line(id, lineRequest.getName(), lineRequest.getColor()));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
