package wooteco.subway.ui;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.SectionResponse;
import wooteco.subway.service.LineService;
import wooteco.subway.service.SectionService;

@RestController
@RequestMapping("/lines")
public class SectionController {
    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<LineResponse> createSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        sectionService.save(sectionRequest, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/sections")
    public ResponseEntity<SectionResponse> findAll(@PathVariable Long id) {
        SectionResponse sectionResponses = sectionService.findAll(id);
        return ResponseEntity.ok().body(sectionResponses);
    }

    @DeleteMapping("/{id}/sections")
    public ResponseEntity<LineResponse> deleteSection(@PathVariable Long id, @RequestParam Long stationId) {
        sectionService.delete(id, stationId);
        return ResponseEntity.ok().build();
    }
}
