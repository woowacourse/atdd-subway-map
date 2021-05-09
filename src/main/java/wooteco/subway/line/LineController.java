package wooteco.subway.line;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.exception.LineElementNullPointException;
import wooteco.subway.exception.LineNameDuplicatedException;
import wooteco.subway.line.service.LineService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody @Valid LineRequest lineRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new LineElementNullPointException();
        }
        Line line = lineService.createLine(lineRequest.getName(), lineRequest.getColor());
        LineResponse lineResponse = new LineResponse(line);
        return ResponseEntity.created(URI.create("/lines/" + line.getId()))
            .body(lineResponse);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineService.findAll();
        List<LineResponse> lineResponses = lines.stream()
            .map(LineResponse::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/lines/{id}")
    public ResponseEntity<LineResponse> findLineById(@PathVariable Long id) {
        Line line = lineService.findLineById(id);
        return ResponseEntity.ok(new LineResponse(line));
    }

    @PutMapping(value = "/lines/{id}")
    public ResponseEntity<Void>updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.findLineById(id);

        final Optional<Line> lineByName = lineService.findLineByName(lineRequest.getName());

        if (lineByName.isPresent() && lineByName.get().isNotSameId(id)) {
            throw new LineNameDuplicatedException();
        }

        lineService.update(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void>removeLine(@PathVariable Long id) {
        lineService.removeLine(id);
        return ResponseEntity.noContent().build();
    }
}
