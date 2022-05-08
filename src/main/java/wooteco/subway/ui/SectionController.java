package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.dto.section.SectionCreationRequest;
import wooteco.subway.dto.section.SectionRequest;
import wooteco.subway.service.SectionService;

@RestController
@RequestMapping("/lines/{lineId}/sections")
public class SectionController {

    private final SectionService sectionService;

    public SectionController(final SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<Void> createSection(@PathVariable final Long lineId,
                                              @RequestBody final SectionRequest request) {
        final SectionCreationRequest creationRequest = new SectionCreationRequest(
                lineId,
                request.getUpStationId(),
                request.getDownStationId(),
                request.getDistance()
        );
        sectionService.save(creationRequest);
        return ResponseEntity.ok().build();
    }
}
