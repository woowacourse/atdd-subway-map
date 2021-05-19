package wooteco.subway.line.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.line.service.dto.LineCreateDto;
import wooteco.subway.line.service.dto.LineDto;
import wooteco.subway.line.controller.dto.LineRequest;
import wooteco.subway.line.controller.dto.LineResponse;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.service.LineService;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> saveLine(@Valid @RequestBody final LineRequest lineRequest) {
        final LineCreateDto lineInfo = lineRequest.toLineCreateDto();

        final LineDto savedLineInfo = lineService.save(lineInfo);

        final LineResponse lineResponse = LineResponse.of(savedLineInfo);
        final Long lineId = lineResponse.getId();
        return ResponseEntity.created(URI.create("/lines/" + lineId)).body(lineResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable final Long id) {
        final LineDto lineInfo = lineService.show(id);

        final LineResponse lineResponse = LineResponse.of(lineInfo);
        return ResponseEntity.ok(lineResponse);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        final List<LineDto> linesInfo = lineService.showAll();

        final List<LineResponse> lineResponses = linesInfo.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lineResponses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> updateLine(@RequestBody final LineRequest lineRequest, @PathVariable final Long id) {
        final Line line = new Line(id, lineRequest);

        lineService.update(line);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable final Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}