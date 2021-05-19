package wooteco.subway.section.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.section.service.SectionService;

@RestController
@RequestMapping("/lines/{id}/sections")
public class SectionController {
    private final SectionService sectionService;

    public SectionController(final SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<Void> addSection(@PathVariable final Long id, @RequestBody SectionRequest sectionRequest) {
        sectionService.save(id, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteSection(@PathVariable final Long id, @RequestParam("stationId") final Long stationId) {
        sectionService.delete(id, stationId);
        return ResponseEntity.noContent().build();
    }
}
