package wooteco.subway.ui;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.dto.SectionDeleteRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.SectionSaveRequest;
import wooteco.subway.service.SectionService;

@RestController
@RequestMapping("/lines/{lineId}/sections")
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping
    public void save(@PathVariable Long lineId, @RequestBody SectionRequest request) {
        SectionSaveRequest saveRequest = SectionSaveRequest.of(lineId, request);
        sectionService.save(saveRequest);
    }

    @DeleteMapping
    public void delete(@PathVariable Long lineId, @RequestParam Long stationId) {
        SectionDeleteRequest deleteRequest = new SectionDeleteRequest(lineId, stationId);
        sectionService.delete(deleteRequest);
    }
}
