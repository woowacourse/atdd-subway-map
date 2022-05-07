package wooteco.subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.dto.SectionSaveRequest;
import wooteco.subway.service.SectionService;

@RestController
@RequestMapping("/lines/{lineId}")
public class SectionController {

    private final SectionService sectionService;

    public SectionController(final SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/sections")
    public ResponseEntity<Void> saveSection(@PathVariable long lineId, @RequestBody SectionSaveRequest sectionSaveRequest) {
        sectionService.save(lineId, sectionSaveRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable long lineId, @RequestParam long stationId) {
        sectionService.delete(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
