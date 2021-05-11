package wooteco.subway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.dao.LineRepository;
import wooteco.subway.dao.SectionRepository;
import wooteco.subway.dao.StationRepository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.service.SectionService;

import java.util.List;

@RestController
@RequestMapping("/lines")
public class SectionController {
    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity<LineResponse> createSection(@PathVariable long lineId, @RequestBody SectionRequest sectionRequest) {
        this.sectionService.createSection(lineId, sectionRequest);

        return ResponseEntity
                .ok()
                .build();
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity deleteSection(@PathVariable long lineId, @RequestParam("stationId") long stationId) {
        this.sectionService.deleteSection(lineId, stationId);

        return ResponseEntity
                .ok()
                .build();
    }
}
