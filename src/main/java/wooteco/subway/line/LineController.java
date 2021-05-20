package wooteco.subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.section.Section;
import wooteco.subway.section.SectionRequest;
import wooteco.subway.section.SectionService;
import wooteco.subway.station.Station;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;
    private final SectionService sectionService;

    @Autowired
    public LineController(LineService lineService, SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line newLine = new Line(lineRequest.getName(), lineRequest.getColor());
        Section section = new Section(
                new Station(lineRequest.getUpStationId()),
                new Station(lineRequest.getDownStationId()),
                lineRequest.getDistance()
        );

        Line line = lineService.createLine(newLine, section);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(new LineResponse(line));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResponses = lineService.showLines().stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("{id}")
    public ResponseEntity<LineResponse> showLineInfo(@PathVariable long id) {
        Line line = lineService.showLineInfo(id);
        return ResponseEntity.ok().body(new LineResponse(line));
    }

    @PutMapping("{id}")
    public ResponseEntity<String> updateLine(@RequestBody LineRequest lineRequest, @PathVariable long id) {
        Line newLine = new Line(lineRequest.getName(), lineRequest.getColor());
        lineService.updateLine(id, newLine);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteLine(@PathVariable long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity<String> addSection(@RequestBody SectionRequest sectionRequest, @PathVariable long lineId) {
        Section newSection = new Section(lineId, sectionRequest);

        sectionService.checkAndAddSection(newSection, new Line(lineId));

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity<String> delete(@PathVariable long lineId, @RequestParam long stationId) {
        sectionService.checkSectionCount(lineId);
        List<Long> upStationIds = sectionService.findUpStationId(lineId, stationId);
        List<Long> downStationIds = sectionService.findDownStationId(lineId, stationId);

        return sectionService.checkUpDownAndDelete(lineId, upStationIds, downStationIds, stationId);

    }
}
