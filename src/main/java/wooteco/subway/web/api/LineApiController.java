package wooteco.subway.web.api;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.badRequest.WrongInformationException;
import wooteco.subway.service.LineService;
import wooteco.subway.web.request.LineRequest;
import wooteco.subway.web.response.LineResponse;

@RequiredArgsConstructor
@RequestMapping("/lines")
@RestController
public class LineApiController {

    private final LineService lineService;

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody @Valid LineRequest lineRequest,
        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new WrongInformationException();
        }
        final Line line = Line.create(lineRequest.getName(), lineRequest.getColor());
        Line createdLine = lineService
            .createLine(line, lineRequest.getUpStationId(), lineRequest.getDownStationId(),
                lineRequest.getDistance());

        return ResponseEntity.created(URI.create("/lines/" + createdLine.getId()))
            .body(LineResponse.create(createdLine));
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineService.findAll();
        List<LineResponse> lineResponses =
            lines.stream()
                .map(LineResponse::create)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> findLineById(@PathVariable Long id) {
        Line line = lineService.findLine(id);
        return ResponseEntity.ok().body(LineResponse.create(line));
    }

    @PutMapping("/{id}")
    public ResponseEntity updateLine(@PathVariable Long id,
        @RequestBody @Valid LineRequest lineRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult);
        }
        lineService.update(id, lineRequest.getName(), lineRequest.getColor());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity removeLine(@PathVariable Long id) {
        lineService.removeLine(id);
        return ResponseEntity.noContent().build();
    }
}
