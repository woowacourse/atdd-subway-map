package wooteco.subway.line.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.line.domain.Line;
import wooteco.subway.section.domain.Section;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.service.LineService;
import wooteco.subway.section.service.SectionService;

import java.net.URI;
import java.util.List;

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
        LineResponse lineResponse = LineResponse.toDto(newLine, section); // TODO: upStationId, downStationId로 이름 찾아서 station response 생성
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
}
