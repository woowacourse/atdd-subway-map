package wooteco.subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.controller.dto.SectionRequest;
import wooteco.subway.controller.dto.SectionResponse;
import wooteco.subway.domain.Section;
import wooteco.subway.service.SectionService;

import javax.validation.Valid;
import java.net.URI;

@Validated
@RequestMapping("/lines/{id}")
@RestController
public class SectionController {

    private final SectionService sectionService;

    public SectionController(final SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<Section> createSection(@PathVariable(name = "id") Long lineId,
                                                 @Valid @RequestBody SectionRequest sectionRequest) {
        Section section = sectionRequest.toEntity(lineId);

        final Section savedSection = sectionService.save(section);

        final URI uri = URI.create(String.format("/lines/%d/sections/%d", lineId, savedSection.getId()));
        return ResponseEntity.created(uri).body(savedSection);
    }

    @GetMapping(value = "/sections/{sectionId}")
    public ResponseEntity<SectionResponse> showSection(@PathVariable(name = "id") Long lineId,
                                                       @PathVariable Long sectionId) {
        final Section section = sectionService.findByLineIdAndId(lineId, sectionId);
        final SectionResponse sectionResponse = new SectionResponse(section);
        return ResponseEntity.ok(sectionResponse);
    }

    @DeleteMapping(value = "/sections")
    public ResponseEntity<SectionResponse> deleteSection(@PathVariable(name = "id") Long lineId,
                                                         @RequestParam Long stationId) {
        sectionService.deleteSection(new Section(lineId, stationId));
        return ResponseEntity.noContent().build();
    }
}
