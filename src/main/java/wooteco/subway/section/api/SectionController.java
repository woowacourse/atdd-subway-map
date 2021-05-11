package wooteco.subway.section.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.line.SectionRequest;
import wooteco.subway.section.service.SectionService;

import java.net.URI;

@RestController
@RequestMapping("/lines/{lineId}/sections")
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity createSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        sectionService.save(lineId, sectionRequest);
        return ResponseEntity.created(URI.create("/lines/" + lineId)).build();
    }

}
