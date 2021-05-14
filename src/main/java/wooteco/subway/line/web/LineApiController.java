package wooteco.subway.line.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.line.LineInsufficientRequestException;
import wooteco.subway.line.LineService;
import wooteco.subway.station.StationService;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/lines")
public class LineApiController {

    private final LineService lineService;
    private final StationService stationService;

    @InitBinder("lineRequest")
    private void initBind(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new LineValidator());
    }


    @GetMapping
    public ResponseEntity<List<LineResponse>> showAll() {
        List<Line> lines = lineService.findAll();
        List<LineResponse> responses = new ArrayList<>();
        for (Line line : lines) {
            responses.add(LineResponse.create(line));
        }
        return ResponseEntity.ok().body(responses);
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody @Valid LineRequest lineRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new LineInsufficientRequestException();
        }

        Line line = lineService.create(lineRequest);

        LineResponse lineResponse = LineResponse.create(line);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(lineResponse);
    }

    @GetMapping("/{lineId}")
    public ResponseEntity<LineResponse> readLine(@PathVariable Long lineId) {
        Line line = lineService.find(lineId);
        return ResponseEntity.ok(LineResponse.create(line));

    }
}
