package wooteco.subway.section.controller;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.section.controller.dto.SectionRequest;
import wooteco.subway.section.service.SectionService;
import wooteco.subway.section.service.dto.SectionSaveDto;

@RestController
public class SectionRestController {
    private final SectionService sectionService;
    private final ModelMapper modelMapper;

    public SectionRestController(SectionService sectionService, ModelMapper modelMapper) {
        this.sectionService = sectionService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> createSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        sectionService.save(lineId, modelMapper.map(sectionRequest, SectionSaveDto.class));
        return ResponseEntity.noContent().build();
    }
}
