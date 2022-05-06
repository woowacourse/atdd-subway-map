package wooteco.subway.ui;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.service.LineService;
import wooteco.subway.service.SectionService;
import wooteco.subway.service.StationService;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("/lines")
@RestController
public class LineController {
    private final LineService lineService;
    private final StationService stationService;
    private final SectionService sectionService;

    public LineController(LineService lineService, StationService stationService, SectionService sectionService) {
        this.lineService = lineService;
        this.stationService = stationService;
        this.sectionService = sectionService;
    }

    @PostMapping()
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Station downStation = stationService.findById(lineRequest.getDownStationId());
        Station upStation = stationService.findById(lineRequest.getUpStationId());

        Line newLine = lineService.save(lineRequest.toLine());

        Section section = new Section(newLine.getId(), upStation.getId(), downStation.getId(), lineRequest.getDistance());
        sectionService.save(section);

        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor(), createStationResponse(newLine));
        return ResponseEntity
                .created(URI.create("/lines/" + newLine.getId()))
                .body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineService.findAll();
        List<LineResponse> lineResponses = lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor(), createStationResponse(line)))
                .collect(Collectors.toList());

        return ResponseEntity
                .ok()
                .body(lineResponses);
    }

    private List<StationResponse> createStationResponse(Line line) {
        Set<Long> stationIds = sectionService.findStationIdsIn(line);

        List<Station> stations = stationService.findByIdIn(stationIds);

        return stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> searchLine(@PathVariable Long id) {
        Line line = lineService.findById(id);
        LineResponse lineResponse = new LineResponse(line.getId(), line.getName(), line.getColor(), createStationResponse(line));

        return ResponseEntity
                .ok()
                .body(lineResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> editLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.edit(id, lineRequest.getName(), lineRequest.getColor());

        return ResponseEntity
                .ok()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.delete(id);

        return ResponseEntity
                .noContent()
                .build();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Void> lineNotFound() {
        return ResponseEntity
                .noContent()
                .build();
    }
}
