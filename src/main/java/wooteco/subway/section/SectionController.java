package wooteco.subway.section;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/lines/{lineId}/sections")
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<SectionResponse> addSection(@PathVariable Long lineId,
                                                      @RequestBody @Valid SectionRequest sectionRequest) {
        Section savedSection = sectionService.save(SectionDto.of(lineId, sectionRequest));
        return ResponseEntity.ok(new SectionResponse(savedSection));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        sectionService.delete(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}

