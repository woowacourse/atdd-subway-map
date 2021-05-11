package wooteco.subway.section.controller;

import javax.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.section.service.SectionService;

@RestController
@RequestMapping("/lines/{lineId}/sections")
public class SectionController {
    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createSection(@PathVariable Long lineId, @RequestBody @Valid SectionRequest sectionRequest) {
        sectionService.insertSectionInLine(lineId, sectionRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "")
    public ResponseEntity<Void> deleteStationInLine(@PathVariable Long lineId, @RequestParam Long stationId) {
        sectionService.delete(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
