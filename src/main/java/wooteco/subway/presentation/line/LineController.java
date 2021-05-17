package wooteco.subway.presentation.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.application.line.LineService;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.line.section.Section;
import wooteco.subway.domain.line.section.Sections;
import wooteco.subway.domain.line.value.line.LineColor;
import wooteco.subway.domain.line.value.line.LineId;
import wooteco.subway.domain.line.value.line.LineName;
import wooteco.subway.domain.line.value.section.Distance;
import wooteco.subway.domain.station.value.StationId;
import wooteco.subway.presentation.line.dto.LineDtoAssembler;
import wooteco.subway.presentation.line.dto.LineRequest;
import wooteco.subway.presentation.line.dto.LineResponse;
import wooteco.subway.presentation.line.dto.SectionRequest;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("lines")
public class LineController {

    private final LineDtoAssembler lineDtoAssembler;
    private final LineService lineService;

    public LineController(LineDtoAssembler lineDtoAssembler, LineService lineService) {
        this.lineDtoAssembler = lineDtoAssembler;
        this.lineService = lineService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> createNewLine(@Valid @RequestBody LineRequest lineRequest) {
        final Line line = createNewLineFrom(lineRequest);

        final Line savedLine = lineService.save(line);

        return ResponseEntity
                .created(URI.create("/lines/" + savedLine.getLineId()))
                .body(lineDtoAssembler.line(savedLine));
    }

    private Line createNewLineFrom(LineRequest lineRequest) {
        final Section section = new Section(
                new StationId(lineRequest.getUpStationId()),
                new StationId(lineRequest.getDownStationId()),
                new Distance(lineRequest.getDistance())
        );

        final Sections sections = new Sections(Collections.singletonList(section));

        return new Line(
                new LineName(lineRequest.getName()),
                new LineColor(lineRequest.getColor()),
                sections
        );
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> allLines() {
        final List<LineResponse> lineResponses = lineService.allLines().stream()
                .map(lineDtoAssembler::line)
                .collect(toList());

        return ResponseEntity.ok(lineResponses);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> findById(@PathVariable Long id) {
        final Line line = lineService.findById(id);

        return ResponseEntity.ok(lineDtoAssembler.line(line));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modifyById(@PathVariable Long id,
                                           @Valid @RequestBody LineRequest lineRequest) {
        final Line line = new Line(
                new LineId(id),
                new LineName(lineRequest.getName()),
                new LineColor(lineRequest.getColor())
        );

        lineService.update(line);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        lineService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{lineId}/sections")
    public ResponseEntity<Void> deleteSectionByStationId(@PathVariable Long lineId,
                                                         @RequestParam("stationId") Long stationId) {
        Line line = lineService.findById(lineId);
        line.deleteSectionByStationId(stationId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{lineId}/sections")
    public ResponseEntity<LineResponse> addNewSection(@Valid @RequestBody SectionRequest sectionRequest,
                                                      @PathVariable Long lineId) {
        Section section = new Section(
                new LineId(lineId),
                new StationId(sectionRequest.getUpStationId()),
                new StationId(sectionRequest.getDownStationId()),
                new Distance(sectionRequest.getDistance())
        );

        lineService.addNewSection(lineId, section);
        Line line = lineService.findById(lineId);
        LineResponse line1 = lineDtoAssembler.line(line);

        return ResponseEntity.ok(line1);
    }

}
