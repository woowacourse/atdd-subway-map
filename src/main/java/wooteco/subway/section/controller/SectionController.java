package wooteco.subway.section.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.section.service.SectionService;

@RestController
public class SectionController {
    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> createSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        sectionService.save(lineId, sectionRequest);
        return ResponseEntity.noContent().build();
    }

//    @GetMapping("/lines/{lineId}/sections")
//    public ResponseEntity<Void> showSections(@PathVariable Long lineId) {
//        sectionService.findAllByLineId(lineId);
//        return ResponseEntity.noContent().build();
//    }
}
