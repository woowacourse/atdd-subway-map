package wooteco.subway.web.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.line.DuplicatedLineInformationException;
import wooteco.subway.exception.line.InsufficientLineInformationException;
import wooteco.subway.service.LineService;
import wooteco.subway.service.StationService;
import wooteco.subway.web.request.LineRequest;
import wooteco.subway.web.response.LineResponse;
import wooteco.subway.web.validate.LineValidator;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        Station upStation = stationService.find(lineRequest.getUpStationId());
        Station downStation = stationService.find(lineRequest.getDownStationId());

        Line line = lineService.create(lineRequest, upStation, downStation);
        LineResponse lineResponse = LineResponse.of(line);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> readLines() {
        List<Line> lines = lineService.findAll();
        List<LineResponse> lineResponses = lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lineResponses);
    }

    @GetMapping("/{lineId}")
    public ResponseEntity<LineResponse> readLine(@PathVariable Long lineId) {
        Line line = lineService.find(lineId);
        return ResponseEntity.ok(LineResponse.of(line));
    }

    @PutMapping("/{lineId}")
    public ResponseEntity<Long> updateLine(@PathVariable Long lineId, @RequestBody LineRequest lineRequest) {
        lineService.find(lineId);

        final Optional<Line> lineByName = lineService.findByNameOrColor(lineRequest);

        if (lineByName.isPresent() && lineByName.get().isNotSameId(lineId)) {
            throw new DuplicatedLineInformationException();
        }
        lineService.update(lineId, lineRequest);
        return ResponseEntity.ok(lineId);
    }

    @DeleteMapping("/{lineId}")
    public ResponseEntity<Long> delete(@PathVariable Long lineId) {
        lineService.delete(lineId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(lineId);
    }
}
