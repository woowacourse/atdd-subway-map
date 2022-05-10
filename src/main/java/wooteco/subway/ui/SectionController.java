package wooteco.subway.ui;

import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.service.SectionService;

@RestController
@RequestMapping("/lines/{lineId}/sections")
public class SectionController {
    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<Void> createSection(@PathVariable Long lineId,
                                              @RequestBody @Valid SectionRequest sectionRequest) {
        sectionService.create(new Section(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
                sectionRequest.getDistance()));
        return ResponseEntity.ok().build();
    }
}
