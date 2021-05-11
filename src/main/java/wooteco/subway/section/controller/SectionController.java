package wooteco.subway.section.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.line.Line;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.service.LineService;
import wooteco.subway.section.Section;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.section.service.SectionService;

@RestController
@RequestMapping("/lines/{lineId}/sections")
public class SectionController {
    private final SectionService sectionService;
    private final LineService lineService;

    @Autowired
    public SectionController(SectionService sectionService, LineService lineService) {
        this.sectionService = sectionService;
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> addSection(@PathVariable long lineId,
        @RequestBody SectionRequest sectionRequest) {
        Section section = new Section(lineId, sectionRequest);
        sectionService.addSection(section);

        Line line = lineService.showLine(lineId);
        LineResponse lineResponse = new LineResponse(line);
        return ResponseEntity.created(URI.create("/lines/" + lineId)).body(lineResponse);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteSection(@PathVariable long lineId, @RequestParam long stationId) {
        sectionService.deleteSectionByStationId(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
