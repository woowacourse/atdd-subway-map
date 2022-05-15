package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.service.SectionService;
import wooteco.subway.service.dto.LineServiceResponse;
import wooteco.subway.service.dto.SectionServiceDeleteRequest;
import wooteco.subway.ui.dto.SectionRequest;

@RestController
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/lines/{id}/sections")
    public ResponseEntity<LineServiceResponse> createSection(
        @Validated @RequestBody SectionRequest sectionRequest, @PathVariable Long id) {
        Long savedId = sectionService.save(sectionRequest.toServiceRequest(), id);
        if (savedId != null) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/lines/{id}/sections")
    public ResponseEntity<LineServiceResponse> deleteSection(@PathVariable Long id,
        @RequestParam Long stationId) {
        if (sectionService.removeSection(new SectionServiceDeleteRequest(id, stationId))) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.noContent().build();
    }
}
