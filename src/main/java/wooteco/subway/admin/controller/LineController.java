package wooteco.subway.admin.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
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

	@GetMapping("/lines/{id}")
	public ResponseEntity findLineById(@PathVariable Long id) {
		return ResponseEntity.ok(LineResponse.of(lineService.findLineById(id)));
	}

	@PutMapping("/lines/{id}")
	public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
		lineService.updateLine(id,lineRequest.toLine());
		return ResponseEntity.ok().build();
	}
}
