package wooteco.subway.admin.controller;

import java.util.List;
import java.util.stream.Collectors;

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

	@PostMapping
	public ResponseEntity createLines(@RequestBody LineRequest lineRequest) {
		lineService.save(lineRequest.toLine());
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping
	public List<LineResponse> findLines() {
		return lineService.showLines().stream()
			.map(LineResponse::of)
			.collect(Collectors.toList());
	}

	@GetMapping("/{id}")
	public LineResponse findLine(@PathVariable Long id) {
		return lineService.findById(id);
	}

	@PutMapping("/{id}")
	public ResponseEntity update(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
		lineService.updateLine(id, lineRequest.toLine());
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		lineService.deleteLineById(id);
	}
}
