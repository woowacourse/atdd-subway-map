package wooteco.subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.controller.dto.ControllerDtoAssembler;
import wooteco.subway.controller.dto.section.SectionRequest;
import wooteco.subway.service.SectionService;

@RestController
@RequestMapping("/lines/{lineId}/sections")
public class SectionController {

    private SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<Void> createSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest){
        sectionService.create(ControllerDtoAssembler.sectionRequestDto(lineId, sectionRequest));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteSection(@PathVariable Long lineId, @RequestParam Long stationId){
        sectionService.delete(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
