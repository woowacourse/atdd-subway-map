package wooteco.subway.line;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.section.SectionRequest;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@Valid @RequestBody LineRequest lineRequest) {
        long upStationId = lineRequest.getUpStationId();
        long downStationId = lineRequest.getDownStationId();
        String lineName = lineRequest.getName();
        String lineColor = lineRequest.getColor();
        int distance = lineRequest.getDistance();

        LineResponse lineResponse = lineService.createLine(upStationId, downStationId, lineName, lineColor, distance);
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResponses = lineService.showLines();
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable long id) {
        LineResponse lineResponse = lineService.showLine(id);
        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping("{id}")
    public ResponseEntity<String> updateLine(@PathVariable long id, @RequestBody LineRequest lineRequest) {
        String lineName = lineRequest.getName();
        String lineColor = lineRequest.getColor();

        lineService.updateLine(id, lineName, lineColor);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteLine(@PathVariable long id) {
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("{id}/sections")
    public ResponseEntity<String> createSection(@PathVariable long id, @RequestBody SectionRequest sectionRequest) {
        long upStationId = sectionRequest.getUpStationId();
        long downStationId = sectionRequest.getDownStationId();
        int distance = sectionRequest.getDistance();

        lineService.createSection(id, upStationId, downStationId, distance);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("{id}/sections")
    public ResponseEntity<String> deleteSection(@PathVariable long id, @RequestParam long stationId) {
        lineService.deleteSection(id, stationId);
        return ResponseEntity.noContent().build();
    }
}
