package wooteco.subway.controller.api;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.controller.request.LineRequest;
import wooteco.subway.controller.request.SectionRequest;
import wooteco.subway.controller.response.LineResponse;
import wooteco.subway.controller.response.StationResponse;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;
import wooteco.subway.service.LineService;
import wooteco.subway.service.SectionService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;
    private final SectionService sectionService;

    public LineController(LineService lineService, SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody @Valid LineRequest lineRequest) {
        Line savedLine = lineService.createLine(lineRequest);
        LineResponse lineResponse = LineResponse.from(savedLine);
        URI uri = URI.create("/lines/" + savedLine.getId());
        return ResponseEntity.created(uri)
                .body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineService.findAll();
        List<LineResponse> lineResponses = LineResponse.fromList(lines);
        return ResponseEntity.ok(lineResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line line = lineService.findById(id);
        LineResponse lineResponse = writeLineResponse(line);
        return ResponseEntity.ok(lineResponse);
    }

    private LineResponse writeLineResponse(Line line) {
        Sections sections = line.getSections();
        List<Station> stations = sections.getStations();
        List<StationResponse> stationResponses = StationResponse.fromList(stations);
        return LineResponse.of(line, stationResponses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> editLine(@PathVariable long id, @RequestBody @Valid LineRequest lineRequest) {
        lineService.editLine(id, lineRequest);
        return ResponseEntity.noContent()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent()
                .build();
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<LineResponse> addSection(@PathVariable long id, @RequestBody @Valid SectionRequest sectionRequest) {
        sectionService.addSection(sectionRequest, id);
        Line line = lineService.findById(id);
        LineResponse lineResponse = writeLineResponse(line);
        return ResponseEntity.ok(lineResponse);
    }

    @DeleteMapping("/{id}/sections")
    public ResponseEntity<LineResponse> deleteSection(@PathVariable long id, @RequestParam long stationId) {
        sectionService.deleteSection(id, stationId);
        Line line = lineService.findById(id);
        LineResponse lineResponse = writeLineResponse(line);
        return ResponseEntity.ok(lineResponse);
    }
}
