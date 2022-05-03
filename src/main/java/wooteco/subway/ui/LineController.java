package wooteco.subway.ui;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.ExceptionResponse;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.exception.DuplicatedLineException;
import wooteco.subway.service.LineService;

@RestController
public class LineController {

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createStation(@RequestBody LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line newLine = LineService.save(line);
        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor(), null);
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> findAllLines() {
        List<Line> lines = LineService.findAll();
        List<LineResponse> lineResponses = lines.stream()
                .map(it -> new LineResponse(it.getId(), it.getName(), it.getColor(), null))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }


    @ExceptionHandler(DuplicatedLineException.class)
    public ResponseEntity<ExceptionResponse> handleException(DuplicatedLineException exception) {
        return ResponseEntity.badRequest().body(new ExceptionResponse(exception.getMessage()));

    }
}