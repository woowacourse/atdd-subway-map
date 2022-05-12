package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.service.SectionsService;

@RestController
@RequestMapping("/lines")
public class SectionController {
    private final SectionsService sectionsService;

    public SectionController(SectionsService sectionsService) {
        this.sectionsService = sectionsService;
    }


    @PostMapping("/{id}/sections")
    public ResponseEntity<Void> addSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        sectionsService.save(sectionRequest.toEntity(id));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable Long id, @RequestParam Long stationId) {
        sectionsService.delete(id, stationId);
        return ResponseEntity.ok().build();
    }
}
