package wooteco.subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.service.SectionService;

@Controller
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> saveSection(@RequestBody SectionRequest sectionRequest, @PathVariable Long lineId) {
        sectionService.save(sectionRequest, lineId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> deleteSection(@RequestParam Long stationId, @PathVariable Long lineId) {
        sectionService.delete(lineId, stationId);
        return ResponseEntity.ok().build();
    }

}
