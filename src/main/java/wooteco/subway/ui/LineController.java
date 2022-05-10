package wooteco.subway.ui;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.dto.line.LineCreateRequest;
import wooteco.subway.dto.line.LineRequest;
import wooteco.subway.dto.line.LineResponse;
import wooteco.subway.service.LineService;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineCreateRequest lineCreateRequest) {
        LineResponse lineResponse = lineService.save(lineCreateRequest);
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<LineResponse> showLines() {
        return lineService.findAll();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public LineResponse showLine(@PathVariable Long id) {
        return lineService.findById(id);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteLine(@PathVariable Long id) {
        lineService.deleteById(id);
    }

    @PutMapping(value = "/{id}")
    public void updateLine(@PathVariable Long id, @RequestBody LineRequest LineRequest) {
        lineService.update(id, LineRequest);
    }
}
