package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationRequest;
import wooteco.subway.admin.dto.LineStationResponse;
import wooteco.subway.admin.service.LineStationService;

@RestController
@RequestMapping("/line-stations")
public class LineStationController {
	private final LineStationService lineStationService;

	public LineStationController(LineStationService lineStationService) {
		this.lineStationService = lineStationService;
	}

	@GetMapping
	public ResponseEntity<List<LineResponse>> lineStations() {
		return ResponseEntity.ok(lineStationService.findAll());
	}

	@PostMapping
	public ResponseEntity<LineStationResponse> create(
			@RequestBody LineStationRequest request) {
		LineStationResponse created = lineStationService.create(request);
		return ResponseEntity
				.created(URI.create("/lineStations/" + created.getCustomId()))
				.body(created);
	}

	@DeleteMapping("/{lineId}/stations/{stationId}")
	public ResponseEntity<LineStationResponse> delete(
			@PathVariable Long lineId,
			@PathVariable Long stationId) {
		return ResponseEntity.ok(lineStationService.remove(lineId, stationId));
	}
}
