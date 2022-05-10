package wooteco.subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import wooteco.subway.dto.request.SectionRequestDto;

@Controller
@RequestMapping("/lines/{lineId}/sections")
public class SectionController {

    private final SectionService service;

    public SectionController(final SectionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity createSection(@PathVariable final Long lineId,
                                        @RequestBody final SectionRequestDto sectionRequestDto) {
        service.register(lineId, sectionRequestDto);
        return ResponseEntity.ok().build();
    }
}
