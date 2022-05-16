package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.service.LineService;
import wooteco.subway.service.SectionService;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;
    private final SectionService sectionService;

    public LineController(final LineService lineService, final SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody @Valid final LineRequest lineRequest) {
        final Line line = lineRequest.toEntity();

        final Line newLine = lineService.createLine(line, lineRequest.toSectionEntity());
        final List<Station> stations = getStationsByLine(newLine);
        final LineResponse lineResponse = LineResponse.from(newLine, stations);

        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        final List<Line> lines = lineService.getAllLines();
        final List<LineResponse> lineResponses = lines.stream()
                .map(line -> LineResponse.from(line, getStationsByLine(line)))
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable final Long id) {
        final Line line = lineService.getLineById(id);
        final LineResponse lineResponse = LineResponse.from(line, getStationsByLine(line));

        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable final Long id,
                                           @RequestBody @Valid final LineRequest lineRequest) {
        final Line line = lineRequest.toEntity();
        lineService.update(id, line);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable final Long id) {
        lineService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sections")
    public void addSection(@PathVariable final Long id, @RequestBody @Valid final SectionRequest sectionRequest) {
        sectionService.addSection(id, SectionRequest.toEntity(sectionRequest));
    }

    @DeleteMapping("/{id}/sections")
    public void deleteSection(@PathVariable final Long id, @RequestParam final Long stationId) {
        sectionService.delete(id, stationId);
    }

    private List<Station> getStationsByLine(final Line line) {
        return sectionService.getStationsByLine(line.getId());
    }
}
