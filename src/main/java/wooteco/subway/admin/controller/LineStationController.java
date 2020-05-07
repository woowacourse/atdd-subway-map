package wooteco.subway.admin.controller;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.service.LineService;

@RestController
public class LineStationController {
	private LineService service;

	public LineStationController(LineService service) {
		this.service = service;
	}

	@PostMapping("/lineStation/{id}")
	public ResponseEntity addLineStation(@PathVariable Long id, @RequestBody LineStationCreateRequest view) {
		service.addLineStation(id, view);
		Line line = service.showLine(id);

		return ResponseEntity.ok().body(LineResponse.of(line));
	}
}
