package wooteco.subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.controller.dto.SectionRequest;
import wooteco.subway.controller.dto.SectionResponse;
import wooteco.subway.domain.Section;
import wooteco.subway.service.SectionService;

import javax.validation.Valid;
import java.net.URI;

@RequestMapping("/lines/{id}")
@RestController
public class SectionController {

    private final SectionService sectionService;

    public SectionController(final SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<Section> createSection(@PathVariable Long id,
                                                 @Valid @RequestBody SectionRequest sectionRequest,
                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(bindingResult.getFieldError().getDefaultMessage());
        }
        Section section = sectionRequest.toEntity(id);

        final Section savedSection = sectionService.save(section);

        final URI uri = URI.create(String.format("/lines/%d/sections?sectionId=%d", id, savedSection.getId()));
        return ResponseEntity.created(uri).body(savedSection);
    }

    @GetMapping(value = "/sections")
    public ResponseEntity<SectionResponse> showSection(@PathVariable(name = "id") Long lineId,
                                                       @RequestParam Long sectionId) {
        final Section section = sectionService.findByLineIdAndId(lineId, sectionId);
        final SectionResponse sectionResponse = new SectionResponse(section);
        return ResponseEntity.ok(sectionResponse);
    }
}
