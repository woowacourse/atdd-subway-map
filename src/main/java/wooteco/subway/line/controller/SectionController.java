package wooteco.subway.line.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.line.controller.dto.SectionCreateDto;
import wooteco.subway.line.controller.dto.SectionDeleteDto;
import wooteco.subway.line.controller.dto.SectionRequest;
import wooteco.subway.line.service.SectionService;

@RestController
@RequestMapping("lines/{lineId}/sections")
public class SectionController {

    private final SectionService sectionService;

    public SectionController(final SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<Void> saveSection(@PathVariable final Long lineId, @RequestBody final SectionRequest sectionRequest) {
        final SectionCreateDto sectionInfo = sectionRequest.toSectionCreateDto(lineId);

        sectionService.save(sectionInfo);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteSection(@PathVariable final Long lineId, @RequestParam("stationId") final Long stationId) {
        final SectionDeleteDto sectionInfo = SectionDeleteDto.of(lineId, stationId);

        sectionService.delete(sectionInfo);

        return ResponseEntity.noContent().build();
    }
}
