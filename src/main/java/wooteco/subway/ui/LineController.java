package wooteco.subway.ui;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.application.LineService;
import wooteco.subway.application.SectionService;
import wooteco.subway.application.StationService;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


@RequestMapping("/lines")
@RestController
@AllArgsConstructor
public class LineController {

    private final LineService lineService;

    private final SectionService sectionService;

    private final StationService stationService;

    @PostMapping
    public ResponseEntity<LineResponse> createLines(@RequestBody LineRequest lineRequest) {
        Line savedLine = lineService.saveAndGet(lineRequest.toLine(), lineRequest.toSection());
        LinkedList<Long> sortedStationIds = sectionService.findSortedStationIds(savedLine.getId());
        List<Station> stations = stationService.findByIdIn(sortedStationIds);

        LineResponse lineResponse = new LineResponse(savedLine, stations);
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineService.findAll();
        List<LineResponse> lineResponses = new ArrayList<>();

        for (Line line : lines) {
            LinkedList<Long> sortedStationIds = sectionService.findSortedStationIds(line.getId());
            List<Station> stations = stationService.findByIdIn(sortedStationIds);
            LineResponse lineResponse = new LineResponse(line, stations);
            lineResponses.add(lineResponse);
        }

        return ResponseEntity.ok(lineResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line line = lineService.findById(id);
        LineResponse lineResponse = new LineResponse(line.getId(), line.getName(), line.getColor());
        return ResponseEntity.ok(lineResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long id,
                                                   @RequestBody LineRequest lineRequest) {
        Line line = lineService.update(id, lineRequest.getName(), lineRequest.getColor());
        LineResponse lineResponse = new LineResponse(line.getId(), line.getName(), line.getColor());
        return ResponseEntity.ok(lineResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity<Void> createSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        Section section = sectionRequest.toSection(lineId);
        sectionService.createSection(section);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity<Void> deleteSections(@PathVariable Long lineId, @RequestParam Long stationId) {
        sectionService.deleteSection(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
