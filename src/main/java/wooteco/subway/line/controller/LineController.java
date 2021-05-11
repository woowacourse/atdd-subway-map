package wooteco.subway.line.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.exception.SubwayException;
import wooteco.subway.line.dto.request.LineCreateRequest;
import wooteco.subway.line.dto.request.LineUpdateRequest;
import wooteco.subway.line.dto.response.LineCreateResponse;
import wooteco.subway.line.dto.response.LineResponse;
import wooteco.subway.line.dto.response.LineSectionResponse;
import wooteco.subway.line.dto.response.LineStationsResponse;
import wooteco.subway.line.service.LineService;
import wooteco.subway.section.dto.AddSectionDto;
import wooteco.subway.section.dto.request.SectionCreateRequest;
import wooteco.subway.section.dto.response.SectionCreateResponse;
import wooteco.subway.section.dto.response.SectionResponse;
import wooteco.subway.section.service.SectionService;
import wooteco.subway.station.dto.StationResponse;
import wooteco.subway.station.service.StationService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;
    private final StationService stationService;
    private final SectionService sectionService;

    public LineController(LineService lineService, StationService stationService, SectionService sectionService) {
        this.lineService = lineService;
        this.stationService = stationService;
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<LineSectionResponse> createLine(@RequestBody @Valid LineCreateRequest lineCreateRequest, Errors errors) {
        if (errors.hasErrors()) {
            throw new SubwayException("올바른 값이 아닙니다.");
        }

        SectionCreateRequest sectionCreateRequest = new SectionCreateRequest(lineCreateRequest);
        stationService.checkRightStation(sectionCreateRequest.getUpStationId(), sectionCreateRequest.getDownStationId());
        LineCreateResponse newLine = lineService.save(lineCreateRequest);
        SectionCreateResponse initialSection =
                sectionService.save(newLine.getId(), sectionCreateRequest);

        return ResponseEntity.created(URI.create("/lines/" + newLine.getId()))
                .body(new LineSectionResponse(newLine, initialSection));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> allLines = lineService.findAll();
        return ResponseEntity.ok().body(allLines);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineStationsResponse> showLine(@PathVariable Long id) {
        LineResponse line = lineService.findBy(id);
        List<SectionResponse> sections = sectionService.findAllByLineId(line.getId());
        List<StationResponse> stations = stationService.findStations(sections);
        return ResponseEntity.ok().body(new LineStationsResponse(line, stations));
    }

    @PostMapping(value = "/{id}/sections", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SectionCreateResponse> addSection(@PathVariable Long id, @RequestBody SectionCreateRequest sectionCreateRequest) {
        StationResponse upStation = stationService.findById(sectionCreateRequest.getUpStationId());
        StationResponse downStation = stationService.findById(sectionCreateRequest.getDownStationId());
        AddSectionDto addSectionDto = new AddSectionDto(id, upStation, downStation, sectionCreateRequest.getDistance());
        SectionCreateResponse newSection = sectionService.addSection(addSectionDto);
        return ResponseEntity.created(URI.create("/lines/" + newSection.getLineId() + "/sections/" + newSection.getId()))
                .body(newSection);
    }


    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineUpdateRequest lineUpdateRequest) {
        lineService.update(id, lineUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable Long id, @RequestParam Long stationId) {
        sectionService.deleteSection(id, stationId);
        return ResponseEntity.noContent().build();
    }
}
