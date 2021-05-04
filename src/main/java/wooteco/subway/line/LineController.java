package wooteco.subway.line;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
public class LineController {

    @PostMapping("/lines")
    public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
        Line line = new Line(lineRequest.getColor(), lineRequest.getName());
        Line newLine = LineDao.save(line);
        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LinesResponse>> getLines() {
        List<Line> lines = LineDao.getLines();
        List<LinesResponse> linesResponses = new ArrayList<>();
        for (Line line : lines) {
            linesResponses.add(new LinesResponse(line.getId(), line.getName(), line.getColor()));
        }

        return ResponseEntity.ok().body(linesResponses);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> getLine(@PathVariable final Long id) {

        Line line = LineDao.getLine(id);
        LineResponse lineResponse = new LineResponse(line.getId(), line.getName(), line.getColor());
        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity updateLine(@PathVariable final Long id, @RequestBody LineRequest lineRequest) {
        LineDao.updateLine(id, new Line(id, lineRequest.getColor(), lineRequest.getName()));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable final Long id) {
        LineDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/lines")
    public ResponseEntity deleteLines() {
        LineDao.deleteAll();
        return ResponseEntity.ok().build();
    }
}
