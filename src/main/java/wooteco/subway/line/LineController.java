package wooteco.subway.line;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.exception.DuplicatedLineNameException;
import wooteco.subway.exception.VoidLineException;

@RestController
public class LineController {

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineDaoCache lineDaoCache = new LineDaoCache();
        try {
            Line line = new Line(lineRequest.getName(), lineRequest.getColor());
            Line newLine = lineDaoCache.save(line);
            LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(),
                newLine.getColor());
            return ResponseEntity.created(URI.create("/lines/" + newLine.getId()))
                .body(lineResponse);
        } catch (DuplicatedLineNameException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        LineDaoCache lineDaoCache = new LineDaoCache();
        List<Line> lines = lineDaoCache.findAll();
        List<LineResponse> lineResponses = lines.stream()
            .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
            .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        LineDaoCache lineDaoCache = new LineDaoCache();
        try {
            Line line = lineDaoCache.findOne(id);
            LineResponse lineResponse = new LineResponse(line.getId(), line.getName(),
                line.getColor());
            return ResponseEntity.ok().body(lineResponse);
        } catch (VoidLineException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> updateLine(@RequestBody LineRequest lineRequest,
        @PathVariable Long id) {
        LineDaoCache lineDaoCache = new LineDaoCache();
        try {
            Line line = new Line(id, lineRequest.getName(), lineRequest.getColor());
            lineDaoCache.update(id.intValue(), line);
            return ResponseEntity.ok().build();
        } catch (VoidLineException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        LineDaoCache lineDaoCache = new LineDaoCache();
        try {
            lineDaoCache.delete(id);
            return ResponseEntity.ok().build();
        } catch (VoidLineException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
