package wooteco.subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.service.SectionService;

@RestController
@RequestMapping("/lines")
public class SectionController {
    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity<LineResponse> createSection(@PathVariable long lineId, @RequestBody SectionRequest sectionRequest) {
        this.sectionService.createSection(lineId, sectionRequest);

        return ResponseEntity
                .ok()
                .build();
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity deleteSection(@PathVariable long lineId, @RequestParam("stationId") long stationId) {
        this.sectionService.deleteSection(lineId, stationId);

        return ResponseEntity
                .ok()
                .build();
    }
}
