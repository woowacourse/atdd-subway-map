package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.service.SectionService;

@RestController
public class SectionController {
    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> create(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        sectionService.create(lineId, sectionRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> delete(@PathVariable Long lineId, @RequestParam Long stationId) {
        sectionService.deleteSectionByStationIdInLineId(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
