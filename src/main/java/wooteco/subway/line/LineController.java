package wooteco.subway.line;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody final LineRequest lineRequest) {
        final Line requestedLine = new Line(lineRequest);

        final LineDto createdLineInfo = lineService.save(requestedLine);
        final Long lineId = createdLineInfo.getId();
        final String lineName = createdLineInfo.getName();
        final String lineColor = createdLineInfo.getColor();

        final LineResponse lineResponse = new LineResponse(lineName, lineColor);
        return ResponseEntity.created(URI.create("/lines/" + lineId)).body(lineResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable final Long id) {
        final LineDto lineInfo = lineService.show(id);

        final LineResponse lineResponse = new LineResponse(id, lineInfo.getName(), lineInfo.getColor());
        return ResponseEntity.ok().body(lineResponse);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        final List<LineDto> linesInfo = lineService.showAll();

        final List<LineResponse> lineResponses = linesInfo.stream()
                .map(info -> new LineResponse(info.getId(), info.getName(), info.getColor()))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
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