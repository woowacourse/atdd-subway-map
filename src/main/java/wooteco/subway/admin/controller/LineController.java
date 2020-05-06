package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.service.LineService;

import java.net.URI;
import java.util.List;

@RestController
public class LineController {
	private final LineService lineService;

	public LineController(LineService lineService) {
		this.lineService = lineService;
	}

	@PostMapping("/lines")
	public ResponseEntity createLine(@RequestBody LineRequest view) {
		Line line = view.toLine();
		Line persistLine = lineService.save(line);

		return ResponseEntity
				.created(URI.create("/lines/" + persistLine.getId()))
				.body(LineResponse.of(persistLine));
	}

	@GetMapping("/lines")
	public ResponseEntity showLines() {
		List<Line> persistLines = lineService.showLines();
		List<LineResponse> linesResponse = LineResponse.listOf(persistLines);

		return ResponseEntity
				.ok().body(linesResponse);
	}
}
