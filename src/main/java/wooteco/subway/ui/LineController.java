package wooteco.subway.ui;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.MediaType;
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
import wooteco.subway.service.LineService;
import wooteco.subway.service.dto.LineServiceRequest;
import wooteco.subway.ui.dto.LineRequest;
import wooteco.subway.ui.dto.LineResponse;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line newLine = lineService.save(getLineServiceRequest(lineRequest));
        LineResponse lineResponse = getLineResponse(newLine);
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineService.findAll();
        List<LineResponse> lineResponses = lines.stream()
                .map(this::getLineResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line line = lineService.findById(id);
        return ResponseEntity.ok().body(getLineResponse(line));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.update(id, getLineServiceRequest(lineRequest));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private LineServiceRequest getLineServiceRequest(LineRequest lineRequest) {
        return new LineServiceRequest(null, lineRequest.getName(),
                lineRequest.getColor(), lineRequest.getUpStationId(),
                lineRequest.getDownStationId(), lineRequest.getDistance());
    }

    private LineResponse getLineResponse(Line it) {
        return new LineResponse(it.getId(), it.getName(), it.getColor());
    }
}
