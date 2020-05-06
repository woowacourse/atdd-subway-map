package wooteco.subway.admin.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
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
import wooteco.subway.admin.service.LineService;

@RestController
@RequestMapping("/lines")
public class LineController {
	private final LineService lineService;

	public LineController(LineService lineService) {
		this.lineService = lineService;
	}

	@GetMapping("")
	public ResponseEntity<List<LineResponse>> getLines() {
		List<Line> lines = lineService.showLines();
		List<LineResponse> lineResponses = LineResponse.listOf(lines);

		return new ResponseEntity<>(lineResponses, HttpStatus.OK);
	}

	@PostMapping("")
	public ResponseEntity<Void> createLine(@RequestBody LineRequest request) {
		lineService.save(request.toLine());

		return ResponseEntity
			.status(HttpStatus.CREATED)
			.build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<LineResponse> findById(@PathVariable Long id) {
		LineResponse lineResponse = lineService.findLineWithStationsById(id);
		return new ResponseEntity<>(lineResponse, HttpStatus.OK);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Void> updateLines(@PathVariable Long id,
		@RequestBody LineRequest request) {
		lineService.updateLine(id, request.toLine());
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		lineService.deleteLineById(id);
		return ResponseEntity.ok().build();
	}
}