package wooteco.subway.ui;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.service.SectionService;
import wooteco.subway.service.dto.SectionDeleteRequest;
import wooteco.subway.service.dto.SectionRequest;
import wooteco.subway.service.dto.SectionSaveRequest;

@RestController
@RequestMapping("/lines/{lineId}/sections")
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping
    public void save(@PathVariable Long lineId, @RequestBody @Valid SectionRequest request) {
        SectionSaveRequest saveRequest = SectionSaveRequest.of(lineId, request);
        sectionService.save(saveRequest);
    }

    @DeleteMapping
    public void delete(@PathVariable Long lineId, @RequestParam Long stationId) {
        SectionDeleteRequest deleteRequest = new SectionDeleteRequest(lineId, stationId);
        sectionService.delete(deleteRequest);
    }
}
