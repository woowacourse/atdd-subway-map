package wooteco.subway.section.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.section.service.SectionService;

import java.util.List;

@RestController
@RequestMapping("/lines/{lineId}/sections")
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<Void> insertSection(@PathVariable final Long lineId, @RequestBody final SectionRequest sectionRequest) {
        sectionService.addSection(lineId, sectionRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<List<LineResponse>> deleteSection(@PathVariable final Long lineId, final Long stationId) {
        sectionService.deleteSection(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
