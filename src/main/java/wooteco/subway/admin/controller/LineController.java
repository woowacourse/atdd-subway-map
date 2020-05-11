package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.dto.request.LineCreateRequest;
import wooteco.subway.admin.dto.response.LineResponse;
import wooteco.subway.admin.dto.response.LineWithStationsResponse;
import wooteco.subway.admin.service.LineService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("lines")
public class LineController {
	private final LineService lineService;

	public LineController(LineService lineService) {
		this.lineService = lineService;
	}

	@GetMapping
	public ResponseEntity<List<LineWithStationsResponse>> showLines() {
		List<LineWithStationsResponse> lineWithStationsResponses = lineService.findLines();

		return ResponseEntity
				.ok()
				.body(lineWithStationsResponses);
	}

	@GetMapping("{id}")
	public ResponseEntity<LineWithStationsResponse> findLineWithStationsBy(@PathVariable(name = "id") Long id) {
		LineWithStationsResponse lineWithStationsResponse = lineService.findLineWithStationsBy(id);

		return ResponseEntity
				.ok()
				.body(lineWithStationsResponse);
	}

	@PostMapping
	public ResponseEntity<LineResponse> createLine(@RequestBody LineCreateRequest view) {
		LineResponse lineResponse = lineService.save(view);

		return ResponseEntity
				.created(URI.create("/lines/" + lineResponse.getId()))
				.body(lineResponse);
	}

	@PutMapping("{id}")
	public ResponseEntity<Void> updateLineBy(@PathVariable(name = "id") Long id, @RequestBody LineCreateRequest view) {
		lineService.updateLine(id, view);

		return ResponseEntity
				.noContent()
				.build();
	}

	@DeleteMapping("{id}")
	public ResponseEntity<Void> deleteLineBy(@PathVariable(name = "id") Long id) {
		lineService.deleteLineBy(id);

		return ResponseEntity
				.noContent()
				.build();
	}
}
