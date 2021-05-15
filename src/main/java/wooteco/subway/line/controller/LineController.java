package wooteco.subway.line.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.service.LineService;
import wooteco.subway.line.service.SectionService;
import wooteco.subway.station.dto.StationResponse;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RequestMapping("/lines")
@RestController
public class LineController {

    private final LineService lineService;
    private final SectionService sectionService;

    public LineController(LineService lineService, SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody @Valid LineRequest lineRequest) {
        Long id = lineService.save(lineRequest);
        sectionService.save(id, lineRequest);
        LineResponse newLine = lineService.findById(id);
        return ResponseEntity.created(
                URI.create("/lines/" + id))
                .body(newLine);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok(lineService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> showSections(@PathVariable Long id) {
        LineResponse line = lineService.findById(id); // TODO : 오.. 진짜 개이상하다. LineResponse를 받고 또 다시 생성
        List<StationResponse> section = sectionService.findSectionById(id);
        return ResponseEntity.ok(new LineResponse(line.getId(), line.getName(), line.getColor(), section));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody @Valid LineRequest lineRequest) {
        lineService.update(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<LineResponse> addSection(@PathVariable Long id, @RequestBody @Valid LineRequest lineRequest) {
        sectionService.saveSectionOfExistLine(id, lineRequest);
        LineResponse lineResponse = lineService.findById(id); // TODO : 오.. 진짜 개이상하다. LineResponse 받고 또 다시 생성
        List<StationResponse> section = sectionService.findSectionById(id);
        return ResponseEntity.ok(new LineResponse(id, lineResponse.getName(), lineResponse.getColor(), section));
    }

    @DeleteMapping("/{id}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable Long id, @RequestParam Long stationId) {
        sectionService.deleteSection(id, stationId);
        return ResponseEntity.ok().build();
    }
}
