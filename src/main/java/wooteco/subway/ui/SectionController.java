package wooteco.subway.ui;

import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.service.SectionService;
import wooteco.subway.ui.dto.SectionRequest;

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
        sectionService.save(lineId, sectionRequest.toServiceRequest());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteSectionByLineId(@PathVariable Long lineId, @RequestParam Long stationId) {
        sectionService.deleteByLineIdAndStationId(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
