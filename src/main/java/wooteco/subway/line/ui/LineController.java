package wooteco.subway.line.ui;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.service.LineService;
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

    public LineController(LineService lineService) {
        this.lineService = lineService;
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

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
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

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
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

    public ResponseEntity<Void> modifyById(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        final Line line = new Line(id, lineRequest.getName(), lineRequest.getColor());
        lineService.update(line);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        lineService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<String> duplicationKeyExceptionHandle(Exception e) {
        return ResponseEntity.badRequest().body("동일한 라인을 등록할 수 없습니다");
    }

    @ExceptionHandler(DataAccessException.class)
    private ResponseEntity<String> handleDatabaseExceptions(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandle(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
