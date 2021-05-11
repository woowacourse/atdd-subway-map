package wooteco.subway.line.ui;

import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.domain.Sections;
import wooteco.subway.line.service.LineService;
import wooteco.subway.line.ui.dto.LineCreateRequest;
import wooteco.subway.line.ui.dto.LineModifyRequest;
import wooteco.subway.line.ui.dto.LineResponse;
import wooteco.subway.line.ui.dto.SectionAddRequest;
import wooteco.subway.station.domain.Station;

import java.net.URI;
import java.util.Collections;
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
        final Section section = new Section(lineCreateRequest.getUpStationId(),
                lineCreateRequest.getDownStationId(),
                lineCreateRequest.getDistance());
        final Sections sections = new Sections(Collections.singletonList(section));

        final Line line = new Line(lineCreateRequest.getName(), lineCreateRequest.getColor(), sections);
        final Line savedLine = lineService.create(line);

        final List<Station> stations = lineService.getStations(savedLine.getId());

        return ResponseEntity
                .created(URI.create("/lines/" + savedLine.getId()))
                .body(new LineResponse(savedLine, stations));
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
        final Line line = lineService.findById(id);

        final List<Station> stations = lineService.getStations(line.getId());

        return ResponseEntity.ok(new LineResponse(line, stations));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @SuppressWarnings("rawtypes")
    public ResponseEntity modifyById(@PathVariable Long id, @RequestBody LineModifyRequest lineModifyRequest) {

        final Line line = new Line(id, lineModifyRequest.getName(), lineModifyRequest.getName());
        lineService.update(line);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        lineService.deleteById(id);

        return ResponseEntity.noContent().build();
    }


    @PostMapping(value = "/{id}/sections", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addSectionInLine(@PathVariable final Long id, @RequestBody SectionAddRequest sectionAddRequest) {
        Section section = new Section(sectionAddRequest.getUpStationId(), sectionAddRequest.getDownStationId(), sectionAddRequest.getDistance());
        lineService.addSection(id, section);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler({DataAccessException.class, IllegalArgumentException.class})
    private ResponseEntity<String> handleDatabaseExceptions(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
