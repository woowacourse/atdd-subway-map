package wooteco.subway.ui;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.service.LineService;
import wooteco.subway.service.SectionService;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
public class LineController {
    private final LineService lineService;
    private final SectionService sectionService;

    public LineController(final LineService lineService, final SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody final LineRequest lineRequest) {
        final Line newLine = lineService.create(lineRequest.getName(), lineRequest.getColor());
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId()))
                .body(new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor(), sectionService.getStationsByLineId(newLine.getId())));
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<LineResponse> showLines() {
        final List<Line> lines = lineService.findAll();
        return lines.stream()
                .map(it -> new LineResponse(it.getId(), it.getName(), it.getColor(), sectionService.getStationsByLineId(it.getId())))
                .collect(Collectors.toList());
    }

    @GetMapping("/lines/{id}")
    public LineResponse searchLine(@PathVariable final Long id) {
        final Line line = lineService.findById(id);
        return new LineResponse(line.getId(), line.getName(), line.getColor(), sectionService.getStationsByLineId(line.getId()));
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<Void> editLine(@PathVariable final Long id, @RequestBody final LineRequest lineRequest) {
        lineService.edit(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable final Long id) {
        lineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Void> lineNotFound() {
        return ResponseEntity.badRequest().build();
    }
}
