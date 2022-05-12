package wooteco.subway.ui;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.service.LineService;
import wooteco.subway.service.SectionsService;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;
    private final SectionsService sectionsService;

    public LineController(LineService lineService, SectionsService sectionsService) {
        this.lineService = lineService;
        this.sectionsService = sectionsService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line newLine = lineService.save(lineRequest.toLine());
        sectionsService.save(lineRequest.toSection(newLine.getId()));
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId()))
                .body(LineResponse.of(newLine, sectionsService.findStationsOfLine(newLine.getId())));
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResponses = lineService.findAll()
                .stream()
                .map(line -> LineResponse.of(line, sectionsService.findStationsOfLine(line.getId())))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> findLine(@PathVariable Long id) {
        Line line = lineService.findById(id);
        return ResponseEntity.created(URI.create("/lines/" + line.getId()))
                .body(LineResponse.of(line, sectionsService.findStationsOfLine(line.getId())));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> putLines(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.update(id, lineRequest.toLine());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        sectionsService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }
}
