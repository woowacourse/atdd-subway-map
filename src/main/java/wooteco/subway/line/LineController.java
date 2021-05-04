package wooteco.subway.line;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.print.attribute.standard.Media;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.station.StationResponse;

@RestController
public class LineController {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<LineResponse> exceptionHandler(IllegalArgumentException e) {
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line newLine = LineDao.save(line);
        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(),
            newLine.getColor(), null);
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = LineDao.findAll();
        List<LineResponse> linesResponse = lines
            .stream()
            .map(it -> new LineResponse(it.getId(), it.getName(), it.getColor(), null))
            .collect(Collectors.toList());
        return ResponseEntity.ok().body(linesResponse);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line line = LineDao.findById(id);
        LineResponse lineResponse = new LineResponse(line.getId(), line.getName(), line.getColor(), null);
        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        Line persistedLine = LineDao.findById(id);
        Line updatedLine = new Line(persistedLine.getId(), lineRequest.getName(), lineRequest.getColor());
        LineDao.update(updatedLine);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id){
        LineDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
