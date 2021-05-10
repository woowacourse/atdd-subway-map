package wooteco.subway.section;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lines")
public class SectionController {

    private final SectionService service;

    public SectionController(SectionService service) {
        this.service = service;
    }

    // TODO 구간 추가 API
    @PostMapping("/{id}/sections")
    public ResponseEntity<Void> createSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        return null;
    }

    @DeleteMapping("/{id}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable Long id, @RequestParam("stationId") Long stationId) {
        service.deleteById(id, stationId);
        return ResponseEntity.noContent().build();
    }

}
