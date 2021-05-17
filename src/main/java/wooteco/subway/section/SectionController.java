package wooteco.subway.section;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.line.LineService;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/lines")
public class SectionController {

    private final LineService lineService;
    private final SectionService sectionService;

    public SectionController(LineService lineService, SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity<SectionResponse> createSection(@PathVariable Long lineId,
                                                         @Valid @RequestBody SectionRequest sectionRequest) {
        lineService.validateId(lineId);
        Section addedSection = sectionService.add(lineId, sectionRequest);
        SectionResponse sectionResponse = new SectionResponse(addedSection);
        return ResponseEntity
                .created(URI.create("/lines/" + lineId + "/sections/" + addedSection.getId()))
                .body(sectionResponse);
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable Long lineId,
                                              @RequestParam(value = "stationId") Long stationId) {
        lineService.validateId(lineId);
        sectionService.delete(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
