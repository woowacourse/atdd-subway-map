package wooteco.subway.line.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.service.LineService;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.section.service.SectionService;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.service.StationService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;
    private final SectionService sectionService;
    private final StationService stationService;

    public LineController(final LineService lineService, final SectionService sectionService, final StationService stationService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = new Line(lineRequest.getColor(), lineRequest.getName());
        Line newLine = lineService.save(line);

        Section section = new Section(
                newLine.getId(),
                lineRequest.getUpStationId(),
                lineRequest.getDownStationId(),
                lineRequest.getDistance()
        );
        sectionService.save(section);

        List<Station> upAndDownStations = stationService.getUpAndDownStations(section);
        newLine.addStations(upAndDownStations);

        LineResponse lineResponse = LineResponse.toDto(newLine);
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> getLines() {
        List<Line> lines = lineService.getLines();
        final List<LineResponse> lineResponses = LineResponse.toDtos(lines);
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> getLine(@PathVariable final Long id) {
        Line line = lineService.getLine(id);
        List<Station> allStations = sectionService.getAllStations(id);
        line.setStations(allStations);

        LineResponse lineResponse = LineResponse.toDto(line);
        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable final Long id, @RequestBody LineRequest lineRequest) {
        lineService.updateLine(new Line(id, lineRequest.getColor(), lineRequest.getName()));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable final Long id) {
        lineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<Void> addSection(@PathVariable final Long id, @RequestBody SectionRequest sectionRequest) {
        Section section = new Section(
                id,
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance());
        sectionService.save(section);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable final Long id, @RequestParam("stationId") final Long stationId) {
        sectionService.deleteSection(id, stationId);
        return ResponseEntity.noContent().build();
    }
}
