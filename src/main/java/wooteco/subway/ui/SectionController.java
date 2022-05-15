package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import wooteco.subway.dto.request.SectionRequest;
import wooteco.subway.service.SectionService;

@Controller
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> createSection(@PathVariable final Long lineId, @RequestBody final SectionRequest request) {
        sectionService.saveSection(lineId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable final Long lineId, @RequestParam final Long stationId) {
        sectionService.deleteSection(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
