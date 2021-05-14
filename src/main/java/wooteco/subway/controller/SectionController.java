package wooteco.subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.SectionResponse;
import wooteco.subway.service.SubwayService;

import java.net.URI;

@RestController
@RequestMapping("/lines")
public class SectionController {
    private final SubwayService subwayService;

    public SectionController(final SubwayService subwayService) {
        this.subwayService = subwayService;
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity<SectionResponse> createSection(@RequestBody SectionRequest sectionRequest, @PathVariable Long lineId) {
        SectionResponse sectionResponse = subwayService.createSection(lineId, sectionRequest);
        return ResponseEntity.created(URI.create("/lines/" + lineId + "/sections/" + sectionResponse.getId())).body(sectionResponse);
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity<SectionResponse> deleteSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        subwayService.deleteAdjacentSectionByStationId(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}