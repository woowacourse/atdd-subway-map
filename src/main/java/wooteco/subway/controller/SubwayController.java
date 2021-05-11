package wooteco.subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.controller.request.SectionInsertRequest;
import wooteco.subway.controller.response.LineWithAllSectionsResponse;
import wooteco.subway.exception.line.LineNotFoundException;
import wooteco.subway.service.SubwayService;

import java.net.URI;

@RestController
@RequestMapping("/lines")
public class SubwayController {

    private final SubwayService subwayService;

    public SubwayController(SubwayService subwayService) {
        this.subwayService = subwayService;
    }

    @GetMapping(value = "/{lineId}")
    public ResponseEntity<LineWithAllSectionsResponse> showSubwayLineInformation(@PathVariable Long lineId)
            throws LineNotFoundException {
        LineWithAllSectionsResponse lineWithAllSectionsResponse = new LineWithAllSectionsResponse(subwayService.findAllInfoByLineId(lineId));
        return ResponseEntity.ok().body(lineWithAllSectionsResponse);
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity<LineWithAllSectionsResponse> insertSectionInLine(@PathVariable Long lineId, @RequestBody SectionInsertRequest sectionInsertRequest) {
        subwayService.insertSectionInLine(lineId, sectionInsertRequest);
        LineWithAllSectionsResponse lineWithAllSectionsResponse = new LineWithAllSectionsResponse(subwayService.findAllInfoByLineId(lineId));
        return ResponseEntity.created(URI.create("/lines/" + lineId + "/sections")).body(lineWithAllSectionsResponse);
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity<Void> deleteSectionInLine(@PathVariable Long lineId, @RequestParam("stationId") Long stationId) {
        subwayService.deleteSectionInLine(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
