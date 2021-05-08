package wooteco.subway.line.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.line.InsufficientLineInformationException;
import wooteco.subway.line.LineService;
import wooteco.subway.station.StationService;

import javax.validation.Valid;
import java.net.URI;


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

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody @Valid LineRequest lineRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InsufficientLineInformationException();
        }

        Station upStation = stationService.findStation(lineRequest.getUpStationId());
        Station downStation = stationService.findStation(lineRequest.getDownStationId());

        Line line = lineService.createLine(lineRequest.getName(), lineRequest.getColor(), upStation, downStation, lineRequest.getDistance());
        LineResponse lineResponse = LineResponse.create(line);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(lineResponse);
    }

    @GetMapping("/{lineId}")
    public ResponseEntity<LineResponse> readLine(@PathVariable Long lineId){
        Line line = lineService.findLine(lineId);
        return ResponseEntity.ok(LineResponse.create(line));

    }
}
