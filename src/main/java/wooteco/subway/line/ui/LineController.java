package wooteco.subway.line.ui;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.repository.LineRepositoryImpl;
import wooteco.subway.line.ui.dto.LineRequest;
import wooteco.subway.line.ui.dto.LineResponse;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("lines")
public class LineController {

    private final LineService lineService;

    public LineController() {
        this.lineService =  new LineService(LineRepositoryImpl.getInstance());
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> createNewLine(@RequestBody LineRequest lineRequest) throws URISyntaxException {
        final Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        final Line savedLine = lineService.save(line);

        return ResponseEntity
                .created(
                        new URI("/lines/" + savedLine.getId())
                )
                .body(
                        new LineResponse(
                                savedLine.getId(),
                                savedLine.getName(),
                                savedLine.getColor()
                        )
                );
    }

    @GetMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<LineResponse>> allLines() {
        final List<LineResponse> lineResponses = lineService.allLines().stream()
                .map(line ->
                        new LineResponse(
                                line.getId(),
                                line.getName(),
                                line.getColor()
                        )
                ).collect(toList());

        return ResponseEntity.ok(lineResponses);
    }

    @GetMapping(value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<LineResponse> findById(@PathVariable Long id) {
        final Line line = lineService.findById(id);

        return ResponseEntity.ok(
                new LineResponse(
                    line.getId(),
                    line.getName(),
                    line.getColor()
                )
        );
    }

    @PutMapping(value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @SuppressWarnings("rawtypes")
    public ResponseEntity modifyById(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        final Line line = new Line(id, lineRequest.getName(), lineRequest.getColor());
        lineService.update(line);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @SuppressWarnings("rawtypes")
    public ResponseEntity deleteById(@PathVariable Long id) {
        lineService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    private ResponseEntity<String> handleIllegalArgumentException(Exception e) {
        System.out.println(e.getMessage());

        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
