package wooteco.subway.ui;

import java.net.URI;
import java.util.List;
import javax.validation.Valid;
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
    public ResponseEntity<LineResponse> saveLine(@Valid @RequestBody LineRequest lineRequest) {
        LineResponse response = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + response.getId())).body(response);
    }

    @PostMapping("/{line-id}/sections")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveSection(@PathVariable(value = "line-id") Long lineId,
                            @RequestBody SectionRequest sectionRequest) {
        lineService.saveSection(lineId, sectionRequest);
    }

    @GetMapping
    public List<LineResponse> findLineAll() {
        return lineService.findLineAll();
    }

    @GetMapping("/{id}")
    public LineResponse findLineById(@PathVariable Long id) {
        return lineService.findLineById(id);
    }

    @PutMapping("/{id}")
    public void updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.update(id, lineRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteLineById(@PathVariable Long id) {
        lineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{line-id}/stations")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSectionByLineIdAndStationId(@PathVariable(value = "line-id") Long lineId,
                                                  @RequestParam Long stationId) {
        lineService.deleteSectionByLineIdAndStationId(lineId, stationId);
    }
}
