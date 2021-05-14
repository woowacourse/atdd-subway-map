package wooteco.subway.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.controller.request.LineAndSectionCreateRequest;
import wooteco.subway.controller.request.SectionInsertRequest;
import wooteco.subway.controller.response.LineCreateResponse;
import wooteco.subway.controller.response.LineWithAllSectionsResponse;
import wooteco.subway.exception.line.LineNotFoundException;
import wooteco.subway.service.LineService;
import wooteco.subway.service.SubwayFacade;
import wooteco.subway.service.dto.LineDto;

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
    public ResponseEntity<List<LineWithAllSectionsResponse>> showLines() {
        final List<LineDto> lines = subwayFacade.findAllLines();
        final List<LineWithAllSectionsResponse> lineRetrieveResponses = lines.stream()
                .map(LineWithAllSectionsResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineRetrieveResponses);
    }

    @GetMapping(value = "/{lineId}")
    public ResponseEntity<LineWithAllSectionsResponse> showSubwayLineInformation(@PathVariable Long lineId)
            throws LineNotFoundException {
        LineWithAllSectionsResponse lineWithAllSectionsResponse = new LineWithAllSectionsResponse(subwayFacade.findAllInfoByLineId(lineId));
        return ResponseEntity.ok().body(lineWithAllSectionsResponse);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<LineWithAllSectionsResponse> update(@PathVariable Long id, @RequestBody LineAndSectionCreateRequest lineAndSectionCreateRequest) {
        subwayFacade.updateLineById(id, lineAndSectionCreateRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity<LineWithAllSectionsResponse> insertSectionInLine(@PathVariable Long lineId, @RequestBody SectionInsertRequest sectionInsertRequest) {
        subwayFacade.insertSectionInLine(lineId, sectionInsertRequest);
        LineWithAllSectionsResponse lineWithAllSectionsResponse = new LineWithAllSectionsResponse(subwayFacade.findAllInfoByLineId(lineId));
        return ResponseEntity.created(URI.create("/lines/" + lineId + "/sections")).body(lineWithAllSectionsResponse);
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
