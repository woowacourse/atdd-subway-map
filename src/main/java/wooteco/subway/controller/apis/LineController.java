package wooteco.subway.controller.apis;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.service.LineService;
import wooteco.subway.service.SectionService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line savedLine = lineService.createLine(lineRequest.getName(), lineRequest.getColor(), lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
        LineResponse lineResponse = LineResponse.from(savedLine);
        URI uri = URI.create("/lines/" + savedLine.getId());
        return ResponseEntity.created(uri)
                .body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResponses = lineService.findAll()
                .stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lineResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line line = lineService.findById(id);
        Sections sections = line.getSections();
        List<StationResponse> stationResponses = sections.getStations()
                .stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
        LineResponse lineResponse = LineResponse.of(line, stationResponses);
        return ResponseEntity.ok(lineResponse);
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<LineResponse> editSection(@PathVariable long id, @RequestBody SectionRequest sectionRequest) {
        sectionService.addSection(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance(), id);
        Line line = lineService.findById(id);
        Sections sections = line.getSections();
        List<StationResponse> stationResponses = sections.getStations()
                .stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
        LineResponse lineResponse = LineResponse.of(line, stationResponses);
        return ResponseEntity.ok(lineResponse);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Void> editLine(@PathVariable long id, @RequestBody LineRequest lineRequest) {
        lineService.editLine(id, lineRequest.getName(), lineRequest.getColor());
        return ResponseEntity.noContent()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent()
                .build();
    }

    @DeleteMapping("/{id}/sections")
    public ResponseEntity<LineResponse> deleteSection(@PathVariable long id, @RequestParam long stationId) {
        sectionService.deleteSection(id, stationId);
        Line line = lineService.findById(id);
        Sections sections = line.getSections();
        List<StationResponse> stationResponses = sections.getStations()
                .stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
        LineResponse lineResponse = LineResponse.of(line, stationResponses);
        return ResponseEntity.ok(lineResponse);
    }
}
