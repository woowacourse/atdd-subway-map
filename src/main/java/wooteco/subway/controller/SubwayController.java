package wooteco.subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.controller.request.SectionInsertRequest;
import wooteco.subway.controller.response.LineRetrieveResponse;
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
    public ResponseEntity<LineRetrieveResponse> showSubwayLineInformation(@PathVariable Long id)
            throws LineNotFoundException {
        LineRetrieveResponse lineRetrieveResponse = new LineRetrieveResponse(subwayService.findAllInfoByLineId(id));
        return ResponseEntity.ok().body(lineRetrieveResponse);
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<LineRetrieveResponse> insertSectionInLine(@PathVariable Long id, @RequestBody SectionInsertRequest sectionInsertRequest) {
        subwayService.insertSectionInLine(id, sectionInsertRequest);
        LineRetrieveResponse lineRetrieveResponse = new LineRetrieveResponse();
        return ResponseEntity.created(URI.create("/lines/" + id + "/sections")).body(lineRetrieveResponse);
    }
}
