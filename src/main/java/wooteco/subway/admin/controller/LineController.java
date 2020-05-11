package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.request.LineCreateRequest;
import wooteco.subway.admin.dto.response.LineResponse;
import wooteco.subway.admin.dto.response.LineWithStationsResponse;
import wooteco.subway.admin.service.LineService;

import java.net.URI;
import java.util.List;

@RestController
public class LineController {
	private final LineService lineService;

	public LineController(LineService lineService) {
		this.lineService = lineService;
	}

	@GetMapping("/lines")
	public ResponseEntity showLines() {
		List<LineWithStationsResponse> lineWithStationsResponses = lineService.findLines();

		return ResponseEntity
				.ok()
				.body(lineWithStationsResponses);
	}

	@GetMapping("/lines/{id}")
	public ResponseEntity findLineWithStationsBy(@PathVariable(name = "id") Long id) {
		LineWithStationsResponse lineWithStationsResponse = lineService.findLineWithStationsBy(id);

		return ResponseEntity
				.ok()
				.body(lineWithStationsResponse);
	}

	@PostMapping("/lines")
	public ResponseEntity createLine(@RequestBody LineCreateRequest view) {
		Line line = view.toLine();
		Line persistLine = lineService.save(line);

		return ResponseEntity
				.created(URI.create("/lines/" + persistLine.getId()))
				.body(LineResponse.of(persistLine));
	}

	@PutMapping("/lines/{id}")
	public ResponseEntity updateLineBy(@PathVariable(name = "id") Long id, @RequestBody LineCreateRequest view) {
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
