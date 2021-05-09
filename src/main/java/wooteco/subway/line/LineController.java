package wooteco.subway.line;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.section.Section;
import wooteco.subway.section.SectionService;

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
        Line line = Line.from(lineRequest);
        Section section = LineRequest.from(lineRequest);

        Line createdLine = lineService.createLine(line);
        Section createdSection = sectionService.createSection(createdLine.getId(), section);

        LineResponse lineResponse = LineResponse.from(createdLine);
        return ResponseEntity.created(URI.create("/lines/" + createdSection.getLineId())).body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResponses = lineService.showLines().stream()
            .map(LineResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable long id) {
        LineResponse lineResponse = LineResponse.from(lineService.showLine(id));
        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping("{id}")
    public ResponseEntity<String> updateLine(@RequestBody LineRequest lineRequest, @PathVariable long id) {
        Line line = Line.from(lineRequest);
        lineService.updateLine(id, line);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteLine(@PathVariable long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }
}
