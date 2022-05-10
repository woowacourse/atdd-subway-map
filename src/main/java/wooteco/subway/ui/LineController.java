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

import wooteco.subway.domain.LineEntity;
import wooteco.subway.domain.StationEntity;
import wooteco.subway.service.LineService;
import wooteco.subway.service.SectionService;
import wooteco.subway.service.StationService;
import wooteco.subway.ui.request.LineRequest;
import wooteco.subway.ui.response.LineResponse;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;
    private final StationService stationService;
    private final SectionService sectionService;

    public LineController(LineService lineService, StationService stationService,
        SectionService sectionService) {
        this.lineService = lineService;
        this.stationService = stationService;
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        StationEntity upStationEntity = stationService.findById(lineRequest.getUpStationId());
        StationEntity downStationEntity = stationService.findById(lineRequest.getDownStationId());
        LineEntity newLine = lineService.createLine(lineRequest.toEntity(), lineRequest.getUpStationId(),
            lineRequest.getDownStationId(), lineRequest.getDistance());
        LineResponse lineResponse = new LineResponse(newLine, List.of(upStationEntity, downStationEntity));
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> findLine(@PathVariable Long id) {
        LineEntity line = lineService.findById(id);
        List<StationEntity> stations = sectionService.findStationsByLineId(id);
        return ResponseEntity.ok().body(new LineResponse(line, stations));
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> findLines() {
        List<LineEntity> lines = lineService.findAll();
        List<LineResponse> lineResponses = lines.stream()
            .map(line -> new LineResponse(line, sectionService.findStationsByLineId(line.getId())))
            .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.update(lineRequest.toEntity(id));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
