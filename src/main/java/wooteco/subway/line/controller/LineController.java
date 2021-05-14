package wooteco.subway.line.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.exception.InvalidInsertException;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.service.LineService;
import wooteco.subway.section.dto.SectionRequest;
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
    private final SectionService sectionService;
    private final StationService stationService;

    public LineController(LineService lineService, SectionService sectionService, StationService stationService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody @Valid LineRequest lineReq, Errors error) {
        validateRequestValues(error);
        stationService.validateStations(lineReq.getUpStationId(), lineReq.getDownStationId());

        LineResponse newLine = lineService.save(lineReq);
        SectionRequest sectionReq = new SectionRequest(lineReq);
        sectionService.save(newLine.getId(), sectionReq);

        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(newLine);
    }

    private void validateRequestValues(Errors error) {
        if (error.hasErrors()) {
            throw new InvalidInsertException("비어 있는 값은 있을 수 없습니다.");
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> allLines = lineService.findAll();
        return ResponseEntity.ok().body(allLines);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        LineResponse line = lineService.findById(id);
        List<Long> stationIds = sectionService.findAllSectionsId(id);
        List<StationResponse> stationsByIds = stationService.findStationsByIds(stationIds);
        line.setStations(stationsByIds);
        return ResponseEntity.ok().body(line);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.update(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
