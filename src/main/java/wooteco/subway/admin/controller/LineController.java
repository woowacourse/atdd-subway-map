package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
}
