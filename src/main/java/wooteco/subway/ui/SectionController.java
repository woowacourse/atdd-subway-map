package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.service.SectionService;

@RequestMapping("/lines/{lineId}/sections")
@RestController
public class SectionController {

    private final SectionService sectionService;

    public SectionController(final SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping()
    public ResponseEntity<Void> createSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        sectionService.save(lineId, sectionRequest);
        return ResponseEntity.ok().build();
    }
}
