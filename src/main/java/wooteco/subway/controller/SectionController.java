package wooteco.subway.controller;

import java.net.URI;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import wooteco.subway.controller.dto.SectionRequest;
import wooteco.subway.controller.dto.SectionResponse;
import wooteco.subway.domain.Section;
import wooteco.subway.service.SectionService;

@Validated
@RequestMapping("/lines/{id}")
@RestController
public class SectionController {

    private final SectionService sectionService;

    public SectionController(final SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<SectionResponse> createSection(@PathVariable(name = "id") Long lineId,
                                                         @Valid @RequestBody SectionRequest sectionRequest) {
        final Section savedSection = sectionService.save(lineId, sectionRequest);
        final URI uri = URI.create(String.format("/lines/%d/sections/%d", lineId, savedSection.getId()));
        return ResponseEntity.created(uri).body(new SectionResponse(lineId, savedSection));
    }

    @DeleteMapping(value = "/sections")
    public ResponseEntity<SectionResponse> deleteSection(@PathVariable(name = "id") Long lineId,
                                                         @RequestParam Long stationId) {
        sectionService.deleteSection(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
