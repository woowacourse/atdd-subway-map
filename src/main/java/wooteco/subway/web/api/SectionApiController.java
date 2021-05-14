package wooteco.subway.web.api;

import java.net.URI;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.domain.Section;
import wooteco.subway.service.SectionService;
import wooteco.subway.web.request.SectionRequest;
import wooteco.subway.web.response.SectionResponse;

@RequestMapping("/lines/{lineId}/sections")
@RequiredArgsConstructor
@RestController
public class SectionApiController {

    private final SectionService sectionService;

    @PostMapping
    public ResponseEntity insertSection(@PathVariable Long lineId,
        @RequestBody @Valid SectionRequest sectionRequest) {
        final Section createdSection = sectionService
            .insertSection(lineId, sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(), sectionRequest.getDistance());

        return ResponseEntity
            .created(URI.create("/lines/" + lineId + "/sections/" + createdSection.getId()))
            .body(SectionResponse.create(createdSection));
    }

    @DeleteMapping
    public ResponseEntity dropSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        sectionService.dropSection(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
