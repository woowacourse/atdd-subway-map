package wooteco.subway.admin.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.service.LineService;

@RestController
public class LineController {
	private final LineService lineService;

	public LineController(LineService lineService) {
		this.lineService = lineService;
	}

	@PostMapping("/lines")
	public ResponseEntity save(@RequestBody LineRequest lineRequest) {
		Line persistLine = lineService.save(lineRequest.toLine());
		return ResponseEntity
				.created(URI.create("/lines/" + persistLine.getId()))
				.build();
	}

	@GetMapping("/lines")
	public ResponseEntity showLines() {
		return ResponseEntity.ok(LineResponse.listOf(lineService.showLines()));
	}
}
