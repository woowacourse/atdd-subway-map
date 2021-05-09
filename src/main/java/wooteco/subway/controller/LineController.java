package wooteco.subway.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.controller.dto.LineRequest;
import wooteco.subway.controller.dto.LineResponse;
import wooteco.subway.controller.dto.SectionRequest;
import wooteco.subway.controller.dto.StationResponse;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.service.LineService;
import wooteco.subway.service.SectionService;
import wooteco.subway.service.StationService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/lines")
@RestController
public class LineController {

    private final LineService lineService;
    private final SectionService sectionService;
    private final StationService stationService;

    public LineController(final LineService lineService, final SectionService sectionService,
                          final StationService stationService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@Valid @RequestBody LineRequest lineRequest,
                                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(bindingResult.getFieldError().getDefaultMessage());
        }

        final Line line = lineRequest.toLineEntity();
        final Section section = lineRequest.toSectionEntity();

        final Line newLine = lineService.save(line, section);
        final LineResponse lineResponse = convertLineToLineResponse(newLine);
        final URI uri = URI.create("/lines/" + newLine.getId());
        return ResponseEntity.created(uri).body(lineResponse);
    }

    private LineResponse convertLineToLineResponse(final Line line) {
        final List<StationResponse> stationResponses = lineService.findStationIdsByLineId(line.getId())
                                                                  .stream()
                                                                  .map(stationService::findById)
                                                                  .map(StationResponse::new)
                                                                  .collect(Collectors.toList());

        return new LineResponse(line, stationResponses);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Section> createSection(@PathVariable Long id,
                                                 @Valid @RequestBody SectionRequest sectionRequest,
                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(bindingResult.getFieldError().getDefaultMessage());
        }

        Section section = sectionRequest.toEntity(id);
        final Section savedSection = sectionService.save(section);
        final URI uri = URI.create(String.format("/lines/%d/%d", id, savedSection.getId()));
        return ResponseEntity.created(uri).body(savedSection);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        final List<Line> lines = lineService.findAll();
        final List<LineResponse> lineResponses = lines.stream()
                                                      .map(this::convertLineToLineResponse)
                                                      .collect(Collectors.toList());

        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        final Line line = lineService.findById(id);
        final LineResponse lineResponse = convertLineToLineResponse(line);
        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> editLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        final Line line = lineRequest.toLineEntity();
        lineService.update(id, line);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
