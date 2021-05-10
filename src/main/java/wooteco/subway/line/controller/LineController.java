package wooteco.subway.line.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.exception.SubwayException;
import wooteco.subway.line.dto.request.LineCreateRequest;
import wooteco.subway.line.dto.request.LineUpdateRequest;
import wooteco.subway.line.dto.response.LineCreateResponse;
import wooteco.subway.line.dto.response.LineResponse;
import wooteco.subway.line.service.LineService;
import wooteco.subway.section.dto.request.SectionCreateRequest;
import wooteco.subway.section.dto.response.SectionCreateResponse;
import wooteco.subway.section.service.SectionService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

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
    public ResponseEntity<LineCreateResponse> createLine(@RequestBody @Valid LineCreateRequest lineCreateRequest, Errors errors) {
        if (errors.hasErrors()) {
            throw new SubwayException("올바른 값이 아닙니다.");
        }

        LineResponse newLine = lineService.save(lineCreateRequest);
        SectionCreateResponse initialSection =
                sectionService.save(newLine, new SectionCreateRequest(lineCreateRequest));

        return ResponseEntity.created(URI.create("/lines/" + newLine.getId()))
                .body(new LineCreateResponse(newLine, initialSection));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> allLines = lineService.findAll();
        return ResponseEntity.ok().body(allLines);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        LineResponse line = lineService.findBy(id);
        return ResponseEntity.ok().body(line);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineUpdateRequest lineUpdateRequest) {
        lineService.update(id, lineUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
