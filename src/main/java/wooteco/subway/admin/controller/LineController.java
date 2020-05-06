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

@RestController
public class LineController {
	private LineService service;

	public LineController(LineService service) {
		this.service = service;
	}

	@PostMapping("/lines")
	public ResponseEntity createLine(@RequestBody LineRequest view) {
		Line persistLine = service.save(view.toLine());

		return ResponseEntity.created(URI.create("/lines/" + persistLine.getId()))
			.body(LineResponse.of(persistLine));
	}

	@GetMapping("/lines")
	public ResponseEntity showLines() {
		return ResponseEntity.ok().body(LineResponse.listOf(service.showLines()));
	}

	@GetMapping("/lines/{id}")
	public ResponseEntity showLine(@PathVariable Long id) {
		return ResponseEntity.ok().body(LineResponse.of(service.showLine(id)));
	}

	@PutMapping("/lines/{id}")
	public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest view) {
		Line line = view.toLine();
		service.updateLine(id, line);

		return ResponseEntity.ok().body(LineResponse.of(line));
	}

	@DeleteMapping("/lines/{id}")
	public ResponseEntity deleteLine(@PathVariable Long id) {
		service.deleteLineById(id);
		return ResponseEntity.noContent().build();
	}
}
