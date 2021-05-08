package wooteco.subway.controller;

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
import wooteco.subway.controller.dto.request.section.SectionCreateRequestDto;
import wooteco.subway.controller.dto.response.section.SectionCreateResponseDto;
import wooteco.subway.service.section.SectionCreateService;
import wooteco.subway.service.section.SectionDeleteService;

@RequestMapping("/lines")
@RestController
public class SectionController {
    private final SectionCreateService sectionCreateService;
    private final SectionDeleteService sectionDeleteService;

    public SectionController(SectionCreateService sectionCreateService, SectionDeleteService sectionDeleteService) {
        this.sectionCreateService = sectionCreateService;
        this.sectionDeleteService = sectionDeleteService;
    }

    @PostMapping(value = "/{lineId}/sections",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SectionCreateResponseDto> createSection(
        @PathVariable Long lineId,
        @RequestBody SectionCreateRequestDto sectionCreateRequestDto) {

        SectionCreateResponseDto sectionCreateResponseDto = sectionCreateService.createSection(lineId, sectionCreateRequestDto);
        return ResponseEntity
            .created(URI.create("/lines/" + lineId + "/sections/" + sectionCreateResponseDto.getId()))
            .body(sectionCreateResponseDto);
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity<Void> deleteSectionById(@PathVariable Long lineId, @RequestParam Long stationId) {
        sectionDeleteService.deleteSectionById(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
