package wooteco.subway.controller.apis;

import java.net.URI;
import java.util.Arrays;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.domain.section.Section;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.SectionResponse;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.service.SectionService;
import wooteco.subway.service.StationService;

@RestController
@RequestMapping("/lines")
public class SectionController {

    private final SectionService sectionService;
    private final StationService stationService;

    public SectionController(SectionService sectionService,
        StationService stationService) {
        this.sectionService = sectionService;
        this.stationService = stationService;
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<SectionResponse> createSection(@PathVariable Long id,
        @RequestBody SectionRequest sectionRequest) {
        Section section = sectionService.createSection(sectionRequest.toSection(), id);
        SectionResponse sectionResponse = new SectionResponse(
            Arrays.asList(
                new StationResponse(stationService.findById(section.getUpStationId())),
                new StationResponse(stationService.findById(section.getDownStationId()))
            ),
            section.getDistance()
        );
        return ResponseEntity.created(URI.create("/lines/" + id + "/sections/" + section.getId()))
            .body(sectionResponse);
    }
}
