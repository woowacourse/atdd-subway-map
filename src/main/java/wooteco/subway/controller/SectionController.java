package wooteco.subway.controller;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.dto.section.SectionSaveRequest;
import wooteco.subway.service.SectionService;

@RestController
@RequestMapping("/lines/{lineId}/sections")
@Validated
public class SectionController {

    private final SectionService sectionService;

    public SectionController(final SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<Void> saveSection(@PathVariable @Positive(message = "노선의 id는 양수 값만 들어올 수 있습니다.") long lineId,
                                            @RequestBody @Valid SectionSaveRequest sectionSaveRequest) {
        sectionService.save(lineId, sectionSaveRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteSection(
            @PathVariable @Positive(message = "노선의 id는 양수 값만 들어올 수 있습니다.") long lineId,
            @RequestParam @Positive(message = "역의 id는 양수 값만 들어올 수 있습니다.") long stationId) {
        sectionService.delete(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
