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

import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.service.NewLineService;

@RestController
@RequestMapping("/lines")
public class LineController {
	private final NewLineService newLineService;

	public LineController(NewLineService newLineService) {
		this.newLineService = newLineService;
	}

	@GetMapping
	public ResponseEntity<List<LineResponse>> lines() {
		return ResponseEntity.ok(newLineService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<LineResponse> line(
			@PathVariable Long id) {
		return ResponseEntity.ok(newLineService.findById(id));
	}

	@PostMapping
	public ResponseEntity<LineResponse> create(
			@RequestBody LineRequest request) {
		LineResponse created = newLineService.create(request);
		return ResponseEntity
				.created(URI.create("/lines/" + created.getId()))
				.body(created);
	}

	@PutMapping("/{id}")
	public ResponseEntity<LineResponse> update(
			@PathVariable Long id, @RequestBody LineRequest request) {
		return ResponseEntity.ok(newLineService.update(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(
			@PathVariable Long id) {
		newLineService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
