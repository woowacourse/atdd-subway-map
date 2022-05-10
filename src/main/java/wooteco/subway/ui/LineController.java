package wooteco.subway.ui;

import java.net.URI;
import java.util.ArrayList;
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
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.service.LineService;
import wooteco.subway.service.SectionService;
import wooteco.subway.service.StationService;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;
    private final SectionService sectionService;
    private final StationService stationService;

    public LineController(LineService lineService, SectionService sectionService, StationService stationService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = lineRequest.toLine();
        Line newLine = lineService.save(line);

        Section section = lineRequest.toSection(newLine.getId());
        Section newSection = sectionService.save(section);

        List<Long> stationIds = sectionService.findStationIdsByLineId(newLine.getId());
        List<Station> stations = stationService.findStationByIds(stationIds);

        List<StationResponse> stationResponses = StationResponse.from(stations);
        LineResponse lineResponse = LineResponse.of(newLine, stationResponses);

        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> findAllLines() {
        List<LineResponse> lineResponses = new ArrayList<>();
        List<Line> lines = lineService.findAll();
        for (Line line : lines) {
            List<Long> stationIds = sectionService.findStationIdsByLineId(line.getId());
            List<Station> stations = stationService.findStationByIds(stationIds);
            List<StationResponse> stationResponses = StationResponse.from(stations);
            LineResponse lineResponse = LineResponse.of(line, stationResponses);
            lineResponses.add(lineResponse);
        }

        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> findLine(@PathVariable Long id) {
        Line line = lineService.findLineById(id);
        List<Long> stationIds = sectionService.findStationIdsByLineId(line.getId());
        List<Station> stations = stationService.findStationByIds(stationIds);

        List<StationResponse> stationResponses = StationResponse.from(stations);
        LineResponse lineResponse = LineResponse.of(line, stationResponses);
        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        Line line = lineRequest.toEntityWithId(id);
        lineService.update(line);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        lineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}