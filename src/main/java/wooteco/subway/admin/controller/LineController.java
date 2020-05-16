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
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.LineStationResponse;
import wooteco.subway.admin.service.LineService;

@RestController
public class LineController {
	private final LineService lineService;

	public LineController(LineService lineService) {
		this.lineService = lineService;
	}

	@PostMapping("/lines")
	public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
		LineResponse lineResponse = lineService.createLine(lineRequest);

		return ResponseEntity
			.created(URI.create("/lines/" + lineResponse.getId()))
			.body(lineResponse);
	}

	@GetMapping("/lines")
	public ResponseEntity showLines() {
		List<LineResponse> lineResponses = lineService.showLines();

		return ResponseEntity
			.ok()
			.body(lineResponses);
	}

	@GetMapping("/lines/{id}")
	public ResponseEntity showLine(@PathVariable Long id) {
		LineResponse lineResponse = lineService.showLine(id);

		return ResponseEntity
			.ok()
			.body(lineResponse);
	}

	@PutMapping("/lines/{id}")
	public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
		LineResponse lineResponse = lineService.updateLine(id, lineRequest);

		return ResponseEntity
			.ok()
			.body(lineResponse);
	}

	@DeleteMapping("/lines/{id}")
	public ResponseEntity deleteLine(@PathVariable Long id) {
		lineService.deleteLineById(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/line/{lineId}/stations")
	public ResponseEntity addLineStation(@PathVariable Long lineId, @RequestBody
		LineStationCreateRequest lineStationCreateRequest) {
		lineService.addLineStation(lineId, lineStationCreateRequest);

		return ResponseEntity
			.ok()
			.body(LineStationResponse.of(lineStationCreateRequest.toLineStation()));
	}

	@DeleteMapping("/line/{lineId}/stations/{stationId}")
	public ResponseEntity deleteLineStation(@PathVariable Long lineId,
		@PathVariable Long stationId) {
		lineService.removeLineStation(lineId, stationId);
		return ResponseEntity.noContent().build();
	}
}
