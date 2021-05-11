package wooteco.subway.section.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.line.controller.LineResponse;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.service.LineService;
import wooteco.subway.section.domain.OrderedSections;
import wooteco.subway.section.service.SectionService;

import java.net.URI;

@RestController
@RequestMapping("/lines/{lineId}/sections")
public class SectionController {
    private final LineService lineService;
    private final SectionService sectionService;

    public SectionController(LineService lineService, SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        Line line = lineService.findById(lineId);
        sectionService.save(line.getId(), sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
        OrderedSections sections = sectionService.findSections(line.getId());
        return ResponseEntity.created(URI.create("/lines/" + lineId)).body(new LineResponse(line, sections));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteLine(@RequestParam long stationId) {
        sectionService.delete(stationId);
        return ResponseEntity.noContent().build();
    }
}
