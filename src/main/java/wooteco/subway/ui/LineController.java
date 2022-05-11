package wooteco.subway.ui;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.dto.request.SectionRequest;
import wooteco.subway.dto.response.LineResponse;
import wooteco.subway.service.LineService;
import wooteco.subway.service.SectionService;
import wooteco.subway.service.StationService;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;
    private final StationService stationService;
    private final SectionService sectionService;

    public LineController(final LineService lineService, final StationService stationService,
                          final SectionService sectionService) {
        this.lineService = lineService;
        this.stationService = stationService;
        this.sectionService = sectionService;
    }

    @PostMapping()
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        final Line createdLine = lineService.create(lineRequest);
        final List<Station> stations = stationService.findUpAndDownStations(lineRequest);
        final LineResponse lineResponse = new LineResponse(createdLine, stations);
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId()))
            .body(lineResponse);
    }

    @GetMapping()
    public List<LineResponse> findAllLine() {
        final List<Line> lines = lineService.findAll();
        final List<LineResponse> lineResponses = lines.stream()
            .map(line -> new LineResponse(line, sectionService.findUniqueSectionStationsByLineId(line.getId())))
            .collect(Collectors.toList());
        return lineResponses;
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> findLineById(@PathVariable Long id) {
        final Line targetLine = lineService.findById(id);
        final LineResponse lineResponse = new LineResponse(targetLine,
            sectionService.findUniqueSectionStationsByLineId(targetLine.getId()));
        return ResponseEntity.ok(lineResponse);
    }

    @PutMapping("/{id}")
    public void updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.update(id, lineRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent()
            .build();
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<Void> addSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        sectionService.addSection(id, sectionRequest);
        return ResponseEntity.ok().build();
    }
}
