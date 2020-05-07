package wooteco.subway.admin.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.service.LineService;

/**
 *    노선 controller class
 *
 *    @author HyungJu An, Yeongho Park
 */
@RestController
public class LineController {
	private final LineService lineService;

	public LineController(final LineService lineService) {
		this.lineService = lineService;
	}

	@PostMapping("/lines")
	public ResponseEntity createLine(@RequestBody final LineRequest lineRequest) {
		Line line = lineRequest.toLine();
		LineResponse lineResponse = lineService.save(line);

		return ResponseEntity
			.created(URI.create("/lines/" + lineResponse.getId()))
			.body(lineResponse);
	}

	@GetMapping("/lines")
	public ResponseEntity showLines() {
		return ResponseEntity.ok(lineService.showLines());
	}

	@GetMapping("/lines/{id}")
	public ResponseEntity showLine(@PathVariable Long id) {
		return ResponseEntity.ok(lineService.showLine(id));
	}

	@PutMapping("/lines/{id}")
	public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
		return ResponseEntity.ok(lineService.updateLine(id, lineRequest.toLine()));
	}

	@DeleteMapping("/lines/{id}")
	public ResponseEntity deleteLine(@PathVariable Long id) {
		lineService.deleteLineById(id);
		return ResponseEntity.noContent().build();
	}
}
