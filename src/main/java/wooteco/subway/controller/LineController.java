package wooteco.subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.controller.dto.request.LineRequest;
import wooteco.subway.controller.dto.response.LineCreateResponseDto;
import wooteco.subway.service.LineService;

import java.net.URI;

@RestController
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineCreateResponseDto> createLine(@RequestBody LineRequest lineRequest) {
        LineCreateResponseDto lineResponse = lineService.createLine(lineRequest);
        return ResponseEntity.created(
                URI.create("/lines/" + lineResponse.getId())
        ).body(lineResponse);
    }

//    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<List<LineResponse>> showLines() {
//        List<LineResponse> lineResponses = lineService.showLines();
//        return ResponseEntity.ok().body(lineResponses);
//    }
//
//    @GetMapping(value = "/lines/{lineId}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<LineResponse> showLine(@PathVariable Long lineId) {
//        LineResponse lineResponse = lineService.showLine(lineId);
//        return ResponseEntity.ok().body(lineResponse);
//    }
//
//    @PutMapping(value = "/lines/{lineId}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity editLine(@PathVariable Long lineId, @RequestBody LineEditRequest request) {
//        lineService.editLine(lineId, request);
//        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).build();
//    }
//
//    @DeleteMapping("/lines/{lineId}")
//    public ResponseEntity deleteLine(@PathVariable Long lineId) {
//        lineService.deleteLine(lineId);
//        return ResponseEntity.noContent().build();
//    }
}
