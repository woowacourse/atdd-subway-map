package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.service.LineService;
import wooteco.subway.service.SectionService;
import wooteco.subway.service.StationService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LineController {

    private final LineService lineService;

    private final SectionService sectionService;

    private final StationService stationService;

    public LineController(final LineService lineService, final SectionService sectionService, final StationService stationService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
        this.stationService = stationService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody @Valid final LineRequest lineRequest) {
        final Line newLine = lineService.create(lineRequest.toEntity(), lineRequest.toSectionEntity());
        final List<Station> stations = getStationsByLine(newLine.getId());
        final LineResponse lineResponse = LineResponse.from(newLine, stations);

        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    private List<Station> getStationsByLine(final long lineId) {
        return stationService.getAllByLineId(lineId);
    }

    @GetMapping("/lines")
    public List<LineResponse> getAllLines() {
        final List<Line> allLines = lineService.getAll();
        return allLines.stream()
                .map(line -> LineResponse.from(line, getStationsByLine(line.getId())))
                .collect(Collectors.toList());
    }

    @GetMapping("/lines/{lineId}")
    public LineResponse getLineById(@PathVariable final Long lineId) {
        return LineResponse.from(lineService.getById(lineId), stationService.getAllByLineId(lineId));
    }

    @PutMapping("/lines/{lineId}")
    public void updateLine(@PathVariable final Long lineId, @RequestBody @Valid final LineRequest lineRequest) {
        lineService.modify(lineId, lineRequest.toEntity());
    }

    @DeleteMapping("/lines/{lineId}")
    public ResponseEntity<Void> deleteLine(@PathVariable final Long lineId) {
        lineService.remove(lineId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/lines/{id}/sections")
    public void addSection(@PathVariable final Long id, @RequestBody @Valid final SectionRequest sectionRequest) {
        sectionService.create(id, SectionRequest.toEntity(sectionRequest));
    }

    @DeleteMapping("/lines/{id}/sections")
    public void deleteSection(@PathVariable final Long id, @RequestParam final Long stationId) {
        sectionService.remove(id, stationId);
    }
}
