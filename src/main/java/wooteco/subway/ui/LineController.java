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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.LineUpdateRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.service.LineService;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        return ResponseEntity.created(URI.create("/lines")).body(lineService.create(lineRequest));
    }

    @GetMapping("/{id}")
    public LineResponse showLine(@PathVariable Long id) {
        return lineService.showById(id);
    }

    @GetMapping
    public List<LineResponse> showLines() {
        return lineService.showAll();
    }

    @PutMapping("/{id}")
    public void updateLine(@PathVariable Long id, @RequestBody LineUpdateRequest request) {
        lineService.updateById(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLineById(@PathVariable Long id) {
        lineService.removeById(id);
    }

    @PostMapping("/{id}/sections")
    public void createSection(@PathVariable Long id, @RequestBody SectionRequest request) {
        lineService.createSection(id, request);
    }

    @DeleteMapping("/{lineId}/sections")
    public void deleteSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        lineService.delete(lineId, stationId);
    }

}
