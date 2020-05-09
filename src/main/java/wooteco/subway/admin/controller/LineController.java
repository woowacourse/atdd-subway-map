package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineWithStationsResponse;
import wooteco.subway.admin.service.LineService;

import java.net.URI;
import java.util.List;

@RestController
public class LineController {
	private final LineService lineService;

	public LineController(LineService lineService) {
		this.lineService = lineService;
	}

	@PostMapping("/lines")
	public ResponseEntity createLine(@RequestBody LineRequest view) {
		Line line = view.toLine();
		Line persistLine = lineService.save(line);

		return ResponseEntity
				.created(URI.create("/lines/" + persistLine.getId()))
				.body(LineResponse.of(persistLine));
	}

	@GetMapping("/lines")
	public ResponseEntity showLines() {
		List<Line> persistLines = lineService.showLines();
		List<LineResponse> linesResponse = LineResponse.listOf(persistLines);

		return ResponseEntity
				.ok()
				.body(linesResponse);
	}

	@GetMapping("/lines/{id}")
	public ResponseEntity findLineWithStationsBy(@PathVariable(name = "id") Long id) {
		try {
			LineWithStationsResponse lineWithStationsResponse = lineService.findLineWithStationsBy(id);

			return ResponseEntity
					.ok()
					.body(lineWithStationsResponse);
		} catch (IllegalArgumentException e) {
			return ResponseEntity
					.badRequest()
					.body(e.getMessage());
		}
	}

	@PutMapping("/lines/{id}")
	public ResponseEntity updateLineBy(@PathVariable(name = "id") Long id, @RequestBody LineRequest view) {
		Line persistLine = lineService.updateLine(id, view.toLine());
		return ResponseEntity
				.ok()
				.body(LineResponse.of(persistLine));
	}

	@DeleteMapping("/lines/{id}")
	public ResponseEntity deleteLineBy(@PathVariable(name = "id") Long id) {
		lineService.deleteLineBy(id);

		return ResponseEntity
				.ok()
				.build();
	}
}
