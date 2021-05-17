package wooteco.subway.line.ui;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.line.exception.LineNotFoundException;
import wooteco.subway.line.service.LineService;
import wooteco.subway.line.ui.dto.LineCreateRequest;
import wooteco.subway.line.ui.dto.LineModifyRequest;
import wooteco.subway.line.ui.dto.LineResponse;
import wooteco.subway.line.ui.dto.SectionAddRequest;

import java.net.URI;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("lines")
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> createNewLine(@RequestBody LineCreateRequest lineCreateRequest) {
        LineResponse lineResponse = lineService.create(lineCreateRequest);

        return ResponseEntity
                .created(URI.create("/lines/" + lineResponse.getId()))
                .body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> allLines() {
        final List<LineResponse> lineResponses = lineService.allLines().toList().stream()
                .map(line -> new LineResponse(line, lineService.getStations(line.getId())))
                .collect(toList());

        return ResponseEntity.ok(lineResponses);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(lineService.findById(id));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modifyById(@PathVariable Long id, @RequestBody LineModifyRequest lineModifyRequest) {
        lineService.update(id, lineModifyRequest);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        lineService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/sections", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addSection(@PathVariable final Long id,
                                           @RequestBody final SectionAddRequest sectionAddRequest) {

        lineService.addSection(id, sectionAddRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable final Long id, @RequestParam final Long stationId) {
        lineService.deleteSection(id, stationId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler({LineNotFoundException.class, IllegalArgumentException.class})
    private ResponseEntity<String> handleDatabaseExceptions(Exception e) {
        System.out.println(e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
