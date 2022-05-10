package wooteco.subway.ui;

import org.springframework.web.bind.annotation.*;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.service.SectionService;

import javax.validation.Valid;

@RestController
@RequestMapping("/lines")
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/{id}/sections")
       public void addSection(@Valid @RequestBody SectionRequest sectionRequest, @PathVariable long id) {
           sectionService.addSection(sectionRequest, id);
       }

       @DeleteMapping("{id}/sections")
       public void deleteSection(@PathVariable long id, @RequestParam long stationId) {
           sectionService.deleteSection(stationId, id);
       }

}
