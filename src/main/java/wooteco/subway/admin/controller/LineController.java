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
import wooteco.subway.admin.service.LineService;

@RequestMapping("/lines")
@RestController
public class LineController {

	private final LineService lineService;

	public LineController(LineService lineService) {
		this.lineService = lineService;
	}

	@GetMapping
	public ResponseEntity<List<LineResponse>> get() {
		return ResponseEntity.ok()
		                     .body(lineService.findAll());
	}

	@PostMapping
	public ResponseEntity<LineResponse> save(@RequestBody LineRequest request) {
		final LineResponse response = lineService.save(request.toLine());
		return ResponseEntity.created(URI.create("/lines/" + response.getId()))
		                     .body(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<LineResponse> getById(@PathVariable Long id) {
		return ResponseEntity.ok()
		                     .body(lineService.findLineWithStationsById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<LineResponse> modify(@PathVariable Long id,
		@RequestBody LineRequest request) {
		lineService.update(id, request.toLine());
		return ResponseEntity.noContent()
		                     .build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> remove(@PathVariable Long id) {
		lineService.delete(id);
		return ResponseEntity.noContent()
		                     .build();
	}

}
