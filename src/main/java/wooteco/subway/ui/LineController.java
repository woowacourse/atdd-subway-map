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
import wooteco.subway.service.LineService;
import wooteco.subway.service.SectionService;
import wooteco.subway.service.dto.LineResponse;
import wooteco.subway.ui.dto.LineCreateRequest;
import wooteco.subway.ui.dto.LineRequest;
import wooteco.subway.ui.dto.SectionRequest;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;
    private final SectionService sectionService;

    public LineController(LineService lineService, SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
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

    @GetMapping("/{lineId}")
    public LineResponse findLine(@PathVariable Long lineId) {
        return lineService.findById(lineId);
    }

    @PutMapping("/{lineId}")
    public void updateLine(@PathVariable Long lineId, @RequestBody LineRequest lineRequest) {
        lineService.update(lineId, lineRequest);
    }

    @DeleteMapping("/{lineId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long lineId) {
        lineService.deleteById(lineId);
    }

    @PostMapping("/{lineId}/sections")
    public void createSectionById(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        sectionService.create(lineId, sectionRequest);
    }

    @DeleteMapping("/{lineId}/sections")
    public void deleteSectionById(@PathVariable Long lineId, @RequestParam Long stationId) {
        sectionService.deleteById(lineId, stationId);
    }
}
