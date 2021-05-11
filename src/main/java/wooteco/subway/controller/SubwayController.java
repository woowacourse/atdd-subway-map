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

    @GetMapping(value = "/{id}")
    public ResponseEntity<LineWithAllSectionsResponse> showSubwayLineInformation(@PathVariable Long id)
            throws LineNotFoundException {
        LineWithAllSectionsResponse lineWithAllSectionsResponse = new LineWithAllSectionsResponse(subwayService.findAllInfoByLineId(id));
        return ResponseEntity.ok().body(lineWithAllSectionsResponse);
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<LineWithAllSectionsResponse> insertSectionInLine(@PathVariable Long id, @RequestBody SectionInsertRequest sectionInsertRequest) {
        subwayService.insertSectionInLine(id, sectionInsertRequest);
        LineWithAllSectionsResponse lineWithAllSectionsResponse = new LineWithAllSectionsResponse(subwayService.findAllInfoByLineId(id));
        return ResponseEntity.created(URI.create("/lines/" + id + "/sections")).body(lineWithAllSectionsResponse);
    }
}
