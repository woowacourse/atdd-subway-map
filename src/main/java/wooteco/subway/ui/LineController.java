package wooteco.subway.ui;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.dto.request.CreateLineRequest;
import wooteco.subway.dto.request.UpdateLineRequest;
import wooteco.subway.dto.response.LineResponse2;
import wooteco.subway.service.LineService2;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService2 lineService;

    public LineController(LineService2 lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse2> createLine(@RequestBody CreateLineRequest lineRequest) {
        LineResponse2 lineResponse = lineService.save(lineRequest);
        URI location = URI.create("/stations/" + lineResponse.getId());
        return ResponseEntity.created(location).body(lineResponse);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse2>> showLines() {
        List<LineResponse2> lines = lineService.findAll();
        return ResponseEntity.ok().body(lines);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<LineResponse2> showLine(@PathVariable Long id) {
        LineResponse2 linesResponse = lineService.find(id);
        return ResponseEntity.ok().body(linesResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id,
                                           @RequestBody UpdateLineRequest lineRequest) {
        lineService.update(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
