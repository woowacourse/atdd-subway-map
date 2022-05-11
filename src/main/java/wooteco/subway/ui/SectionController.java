package wooteco.subway.ui;

import org.springframework.web.bind.annotation.*;
import wooteco.subway.dto.section.SectionRequest;
import wooteco.subway.service.SectionService;

@RestController
@RequestMapping("/lines/{lineId}/sections")
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping
    public void add(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        sectionService.add(lineId, sectionRequest);
    }
}
