package wooteco.subway.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.controller.dto.request.LineEditRequestDto;
import wooteco.subway.controller.dto.request.LineRequestDto;
import wooteco.subway.controller.dto.response.LineCreateResponseDto;
import wooteco.subway.controller.dto.response.LineFindAllResponseDto;
import wooteco.subway.controller.dto.response.LineFindResponseDto;
import wooteco.subway.service.LineService;

import java.net.URI;
import java.util.List;

@RestController
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineCreateResponseDto> createLine(@RequestBody LineRequestDto lineRequest) {
        LineCreateResponseDto lineResponse = lineService.createLine(lineRequest);
        return ResponseEntity.created(
                URI.create("/lines/" + lineResponse.getId())
        ).body(lineResponse);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineFindAllResponseDto>> showLines() {
        List<LineFindAllResponseDto> lineResponses = lineService.showLines();
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/lines/{lineId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineFindResponseDto> showLine(@PathVariable Long lineId) {
        LineFindResponseDto lineResponse = lineService.showLine(lineId);
        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping(value = "/lines/{lineId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity editLine(@PathVariable Long lineId, @RequestBody LineEditRequestDto request) {
        lineService.editLine(lineId, request);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).build();
    }

    @DeleteMapping("/lines/{lineId}")
    public ResponseEntity deleteLine(@PathVariable Long lineId) {
        lineService.deleteLine(lineId);
        return ResponseEntity.noContent().build();
    }
}
