package wooteco.subway.line.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.exception.line.LineInsufficientRequestException;
import wooteco.subway.line.LineService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/lines")
public class LineApiController {
    private final LineService lineService;

    @GetMapping
    public ResponseEntity<List<LineResponse>> showAll() {
        List<LineResponse> lineResponses = lineService.findAll();

        return ResponseEntity.ok().body(lineResponses);
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody @Valid LineRequest lineRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new LineInsufficientRequestException();
        }
        LineResponse lineResponse = lineService.create(lineRequest);

        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping("/{lineId}")
    public ResponseEntity<LineResponse> readLine(@PathVariable Long lineId) {
        LineResponse lineResponse = lineService.findById(lineId);

        return ResponseEntity.ok().body(lineResponse);
    }
}
