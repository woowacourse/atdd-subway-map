package wooteco.subway.ui;

import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.dto.LineCreateRequest;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.service.LineService;

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
        URI uri = URI.create("/lines/" + lineResponse.getId());
        return ResponseEntity.created(uri).body(lineResponse);
    }

    @GetMapping
    public List<LineResponse> showLines() {
        return lineService.findAll();
    }

    @GetMapping("/{id}")
    public LineResponse findLine(@PathVariable Long id) {
        return lineService.findById(id);
    }

    @PutMapping("/{id}")
    public void updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.update(id, lineRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        lineService.deleteById(id);
    }
}
