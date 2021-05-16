package wooteco.subway.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.dto.section.request.SectionInsertRequest;
import wooteco.subway.dto.section.response.SectionInsertResponse;
import wooteco.subway.service.SectionService;

import javax.validation.Valid;
import java.net.URI;

@RequestMapping("/lines/{lineId}/sections")
@RestController
public class SectionController {
    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SectionInsertResponse> createSection(@PathVariable Long lineId, @Valid @RequestBody SectionInsertRequest sectionInsertRequest) {

        SectionInsertResponse sectionInsertResponse = sectionService.add(lineId, sectionInsertRequest);
        return ResponseEntity
                .created(URI.create("/lines/" + lineId + "/sections/" + sectionInsertResponse.getId()))
                .body(sectionInsertResponse);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteSectionById(@PathVariable Long lineId, @RequestParam Long stationId) {
        sectionService.deleteById(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
