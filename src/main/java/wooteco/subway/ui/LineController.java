package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.service.LineService;
import wooteco.subway.service.SectionService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
public class LineController {

    private final LineService lineService;

    private final SectionService sectionService;

    public LineController(final LineService lineService, final SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody @Valid final LineRequest lineRequest) {
        final LineResponse lineResponse = lineService.create(lineRequest);

        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping("/lines")
    public List<LineResponse> getAllLines() {
        return lineService.getAll();
    }

    @GetMapping("/lines/{lineId}")
    public LineResponse getLineById(@PathVariable final Long lineId) {
        return lineService.getById(lineId);
    }

    @PutMapping("/lines/{lineId}")
    public void updateLine(@PathVariable final Long lineId, @RequestBody @Valid final LineRequest lineRequest) {
        lineService.modify(lineId, lineRequest);
    }

    @DeleteMapping("/lines/{lineId}")
    public ResponseEntity<Void> deleteLine(@PathVariable final Long lineId) {
        lineService.remove(lineId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lines/{id}/sections")
    public void addSection(@PathVariable final Long id, @RequestBody @Valid final SectionRequest sectionRequest) {
        sectionService.create(id, sectionRequest);
    }

    @DeleteMapping("/lines/{id}/sections")
    public void deleteSection(@PathVariable final Long id, @RequestParam final Long stationId) {
        sectionService.remove(id, stationId);
    }
}
