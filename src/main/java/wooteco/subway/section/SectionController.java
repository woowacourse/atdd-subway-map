package wooteco.subway.section;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/lines/{lineId}/sections")
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<SectionResponse> addSection(@PathVariable Long lineId,
                                                      @RequestBody SectionRequest sectionRequest) {
        Section savedSection = sectionService.save(SectionDto.of(lineId, sectionRequest));
        return ResponseEntity.ok(new SectionResponse(savedSection));
    }
}

