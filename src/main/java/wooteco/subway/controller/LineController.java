package wooteco.subway.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.controller.request.LineAndSectionCreateRequest;
import wooteco.subway.controller.request.SectionInsertRequest;
import wooteco.subway.controller.response.LineCreateResponse;
import wooteco.subway.controller.response.LineResponse;
import wooteco.subway.service.SubwayFacade;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final SubwayFacade subwayFacade;

    public LineController(SubwayFacade subwayFacade) {
        this.subwayFacade = subwayFacade;
    }

    @PostMapping
    public ResponseEntity<LineCreateResponse> createLine(@RequestBody @Valid LineAndSectionCreateRequest lineAndSectionCreateRequest) {
        LineCreateResponse lineResponse = new LineCreateResponse(subwayFacade.createLine(lineAndSectionCreateRequest));
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        final List<LineResponse> lineRetrieveResponses = subwayFacade.findAllLines().stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineRetrieveResponses);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<LineResponse> update(@PathVariable Long id, @RequestBody LineAndSectionCreateRequest lineAndSectionCreateRequest) {
        subwayFacade.updateLineById(id, lineAndSectionCreateRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{lineId}")
    public ResponseEntity<LineResponse> showSubwayLineInformation(@PathVariable Long lineId) {
        LineResponse lineResponse = new LineResponse(subwayFacade.findAllInfoByLineId(lineId));
        return ResponseEntity.ok().body(lineResponse);
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity<LineResponse> insertSectionInLine(@PathVariable Long lineId, @RequestBody SectionInsertRequest sectionInsertRequest) {
        subwayFacade.insertSectionInLine(lineId, sectionInsertRequest);
        LineResponse lineResponse = new LineResponse(subwayFacade.findAllInfoByLineId(lineId));
        return ResponseEntity.created(URI.create("/lines/" + lineId + "/sections")).body(lineResponse);
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity<Void> deleteSectionInLine(@PathVariable Long lineId, @RequestParam("stationId") Long stationId) {
        subwayFacade.deleteSectionInLine(lineId, stationId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{lineId}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long lineId) {
        subwayFacade.deleteLine(lineId);
        return ResponseEntity.noContent().build();
    }
}
