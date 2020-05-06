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
	private LineRepository lineRepository;

	public LineController(LineRepository lineRepository) {
		this.lineRepository = lineRepository;
	}

	@PostMapping("/lines")
	public ResponseEntity createLine(@RequestBody LineRequest view) {
		Line line = view.toLine();
		Line persistLine = lineRepository.save(line);

		return ResponseEntity.created(URI.create("/lines/" + persistLine.getId()))
			.body(LineResponse.of(persistLine));
	}

	@GetMapping("/lines")
	public ResponseEntity showLines() {
		return ResponseEntity.ok().body(LineResponse.listOf(lineRepository.findAll()));
	}

	@GetMapping("/lines/{id}")
	public ResponseEntity showLine(@PathVariable Long id) {
		Line line = lineRepository.findById(id)
			.orElseThrow(() -> new NoSuchElementException("해당하는 id 의 노선이 존재하지 않습니다."));

		return ResponseEntity.ok().body(LineResponse.of(line));
	}

	@PutMapping("/lines/{id}")
	public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest view) {
		Line line = lineRepository.findById(id)
			.orElseThrow(() -> new NoSuchElementException("해당하는 id 의 노선이 존재하지 않습니다."));
		line.update(view.toLine());
		lineRepository.save(line);

		return ResponseEntity.ok().body(LineResponse.of(line));
	}

	@DeleteMapping("/lines/{id}")
	public ResponseEntity deleteLine(@PathVariable Long id) {
		lineRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
