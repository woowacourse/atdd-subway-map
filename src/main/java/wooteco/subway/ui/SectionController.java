package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.service.SectionService;

@RestController
public class SectionController {

    private final SectionService sectionService;

    public SectionController(final SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/lines/{lineNumber}/sections")
    public ResponseEntity<Void> createSection(@RequestParam final Long lineNumber, @RequestBody final SectionRequest sectionRequest) {
        sectionService.save(lineNumber, sectionRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{lineNumber}/sections")
    public ResponseEntity<Void> deleteSection(@RequestParam final Long lineNumber, @RequestParam final Long stationId) {
        sectionService.deleteById(lineNumber, stationId);
        return ResponseEntity.ok().build();
    }
}
