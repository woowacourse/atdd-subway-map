package wooteco.subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.service.LineService;
import wooteco.subway.service.SectionService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LineController {

    private final LineService lineService;

    private final SectionService sectionService;

    public LineController(LineService lineService, SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody @Valid LineRequest lineRequest) {
        final Line line = lineRequest.toEntity();

        final Line newLine = lineService.create(line, lineRequest.toSectionEntity());
        final List<Station> stations = getStationsByLine(newLine.getId());
        final LineResponse lineResponse = LineResponse.from(newLine, stations);

        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    private List<Station> getStationsByLine(long lineId) {
        return sectionService.getStationsByLine(lineId);
    }

    @GetMapping("/lines")
    public List<LineResponse> getAllLines() {
        List<Line> allLines = lineService.queryAll();
        return allLines.stream()
                .map(line -> LineResponse.from(line, getStationsByLine(line.getId())))
                .collect(Collectors.toList());
    }

    @GetMapping("/lines/{lineId}")
    public LineResponse getLineById(@PathVariable Long lineId) {
        return LineResponse.from(lineService.queryById(lineId), sectionService.getStationsByLine(lineId));
    }

    @PutMapping("/lines/{lineId}")
    public void updateLine(@PathVariable Long lineId, @RequestBody @Valid LineRequest lineRequest) {
        Line newLine = new Line(lineRequest.getName(), lineRequest.getColor());
        lineService.modify(lineId, newLine);
    }

    @DeleteMapping("/lines/{lineId}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long lineId) {
        lineService.remove(lineId);
        return ResponseEntity.noContent().build();
    }
}
