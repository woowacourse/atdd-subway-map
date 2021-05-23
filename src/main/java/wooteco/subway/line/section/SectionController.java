package wooteco.subway.line.section;

import java.net.URI;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.line.section.dto.SectionRequest;

@RestController
@RequestMapping(value = "/lines/{id}/sections", produces = MediaType.APPLICATION_JSON_VALUE)
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<Void> createSection(@PathVariable Long id,
        @RequestBody SectionRequest sectionRequest) {
        sectionService.save(id, sectionRequest);
        return ResponseEntity.created(URI.create("/lines/" + id)).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteSection(@PathVariable Long id,
        @RequestParam("stationId") Long stationId) {
        sectionService.deleteByStationId(id, stationId);
        return ResponseEntity.noContent().build();
    }
}
