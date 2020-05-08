package wooteco.subway.admin.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.service.LineService;

/**
 *    노선 controller class
 *
 *    @author HyungJu An, Yeongho Park
 */
@RequestMapping("/lines")
@RestController
public class LineController {
	private final LineService lineService;

	public LineController(final LineService lineService) {
		this.lineService = lineService;
	}

	@PostMapping
	public ResponseEntity createLine(@RequestBody final LineRequest lineRequest) {
		Line line = lineRequest.toLine();
		LineResponse lineResponse = lineService.save(line);

		return ResponseEntity
			.created(URI.create("/lines/" + lineResponse.getId()))
			.body(lineResponse);
	}

	@GetMapping
	public ResponseEntity showLines() {
		return ResponseEntity.ok(lineService.showLines());
	}

	@GetMapping("/{id}")
	public ResponseEntity showLine(@PathVariable Long id) {
		return ResponseEntity.ok(lineService.findById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
		return ResponseEntity.ok(lineService.updateLine(id, lineRequest.toLine()));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity deleteLine(@PathVariable Long id) {
		lineService.deleteLineById(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{id}/stations")
	public ResponseEntity addLineStation(@PathVariable Long id,
		@RequestBody LineStationCreateRequest lineStationCreateRequest) {
		lineService.addLineStation(id, lineStationCreateRequest);
		return ResponseEntity.created(URI.create("/lines/" + id + "/stations")).build();
	}

	@DeleteMapping("/{lineId}/stations/{stationId}")
	public ResponseEntity removeLineStation(@PathVariable Long lineId, @PathVariable Long stationId) {
		lineService.removeLineStation(lineId, stationId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}/stations")
	public ResponseEntity findLineWithStationsById(@PathVariable Long id) {
		return ResponseEntity.ok(lineService.findLineWithStationsById(id));
	}
}
