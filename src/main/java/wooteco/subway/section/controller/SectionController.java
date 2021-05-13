package wooteco.subway.section.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.section.controller.dto.SectionRequest;
import wooteco.subway.section.servcie.SectionService;

@RestController
@RequestMapping("/lines/{lineId}/sections")
public class SectionController {
    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<Void> addSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        sectionService.addSection(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        sectionService.deleteStationInSection(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
