package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.NoSuchElementException;

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
import wooteco.subway.admin.repository.LineRepository;

@RestController
public class LineController {
	private final LineRepository lineRepository;

	public LineController(LineRepository lineRepository) {
		this.lineRepository = lineRepository;
	}

	@PostMapping("/lines")
	public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
		Line line = lineRequest.toLine();
		if (lineRepository.findByName(line.getName())) {
			return ResponseEntity
				.badRequest()
				.build();
		}

		Line persistLine = lineRepository.save(line);

		return ResponseEntity
			.created(URI.create("/lines/" + persistLine.getId()))
			.body(LineResponse.of(persistLine));
	}

	@GetMapping("/lines")
	public ResponseEntity showLines() {
		return ResponseEntity
			.ok()
			.body(lineRepository.findAll());
	}

	@GetMapping("/lines/{id}")
	public ResponseEntity showLine(@PathVariable Long id) {
		return ResponseEntity
			.ok()
			.body(lineRepository.findById(id));
	}

	@PutMapping("/lines/{id}")
	public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
		Line line = lineRepository.findById(id).orElseThrow(NoSuchElementException::new);
		line.update(lineRequest.toLine());
		Line persistLine = lineRepository.save(line);

		return ResponseEntity
			.ok()
			.body(LineResponse.of(persistLine));
	}

	@DeleteMapping("/lines/{id}")
	public ResponseEntity deleteLine(@PathVariable Long id) {
		lineRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}

}
