package wooteco.subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.service.SectionService;

import java.net.URI;

@RestController
@RequestMapping("/lines")
public class SectionController {
    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity<LineResponse> createSection(@PathVariable long lineId, @RequestBody SectionRequest sectionRequest) {
        LineResponse lineResponse = sectionService.addSection(lineId, sectionRequest);

        URI location = URI.create("/lines/" + lineResponse.getId());
        return ResponseEntity.created(location).body(lineResponse);
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity<LineResponse> deleteSection(@PathVariable long lineId, @RequestParam("stationId") long stationId) {
        sectionService.deleteSection(lineId, stationId);

        return ResponseEntity.noContent().build();
    }
}
