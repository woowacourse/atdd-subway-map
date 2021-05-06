package wooteco.subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    @Autowired
    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLines(@RequestBody LineRequest lineRequest) {
        LineResponse lineResponse = lineService.createLine(lineRequest.getName(), lineRequest.getColor());
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResponses = lineService.findAll();
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> getLine(@PathVariable long id) {
        return ResponseEntity.ok().body(lineService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable long id, @RequestBody LineRequest lineRequest) {
        LineResponse lineResponse = lineService.update(id, lineRequest.getName(), lineRequest.getColor());
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<LineResponse> deleteLine(@PathVariable long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }

}
