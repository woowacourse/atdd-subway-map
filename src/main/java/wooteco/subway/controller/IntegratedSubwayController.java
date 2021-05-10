package wooteco.subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.controller.response.LineRetrieveResponse;
import wooteco.subway.exception.line.LineNotFoundException;
import wooteco.subway.service.IntegratedSubwayService;

@RestController
public class IntegratedSubwayController {

    private final IntegratedSubwayService subwayService;

    public IntegratedSubwayController(IntegratedSubwayService subwayService) {
        this.subwayService = subwayService;
    }

    @GetMapping(value = "/lines/{id}")
    public ResponseEntity<LineRetrieveResponse> showSubwayLineInformation(@PathVariable Long id)
            throws LineNotFoundException {
        LineRetrieveResponse lineRetrieveResponse = new LineRetrieveResponse(subwayService.findAllInfoByLineId(id));
        return ResponseEntity.ok().body(lineRetrieveResponse);
    }
}
