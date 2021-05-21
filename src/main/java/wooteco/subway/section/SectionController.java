package wooteco.subway.section;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/lines")
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity<SectionResponse> createSection(@PathVariable Long lineId,
                                                         @Valid @RequestBody SectionRequest sectionRequest) {
        Section addedSection = sectionService.add(lineId, sectionRequest);
        SectionResponse sectionResponse = new SectionResponse(addedSection);
        return ResponseEntity
                .created(URI.create("/lines/" + lineId + "/sections/" + addedSection.getId()))
                .body(sectionResponse);
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable Long lineId,
                                              @RequestParam(value = "stationId") Long stationId) {
        sectionService.delete(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
