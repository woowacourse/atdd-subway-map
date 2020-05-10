package wooteco.subway.admin.controller;

import java.net.URI;
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

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.Request;
import wooteco.subway.admin.service.LineService;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLines(@RequestBody Request<LineRequest> view) {
        Line line = view.getContent().toLine();
        Line persistLine = lineService.save(line);

        return ResponseEntity
                .created(URI.create("/lines/" + persistLine.getId()))
                .body(LineResponse.of(persistLine));
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineService.showLines();
        List<LineResponse> lineResponses = LineResponse.listOf(lines);
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> getLine(@PathVariable Long id) {
        LineResponse lineResponse = lineService.findLineWithStationsById(id);
        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody Request<LineRequest> lineRequest) {
        lineService.updateLine(id, lineRequest.getContent().toLine());
        return ResponseEntity.ok().build();
    }

	@DeleteMapping("/{id}")
	public ResponseEntity<LineResponse> deleteLine(@PathVariable Long id) {
		lineService.deleteLineById(id);
		return ResponseEntity.noContent().build();
	}
}
