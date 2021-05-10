package wooteco.subway.line.section;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/lines/{lineId}")
@RestController
public class SectionController {

    private final SectionService sectionService;

    public SectionController(final SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/sections")
    public ResponseEntity<SectionResponse> createSection(@PathVariable long lineId, @RequestBody SectionRequest sectionRequest) {
        final SectionResponse sectionResponse = sectionService.createSection(lineId, sectionRequest);
        final String uri = String.format("/lines/%s/sections/%s", lineId, sectionResponse.getId());
        return ResponseEntity.created(URI.create(uri)).body(sectionResponse);
    }
}
