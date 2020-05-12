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
import wooteco.subway.admin.dto.EdgeRequest;
import wooteco.subway.admin.dto.EdgeResponse;
import wooteco.subway.admin.service.EdgeService;

@RestController
@RequestMapping("/edges")
public class EdgeController {
	private final EdgeService edgeService;

	public EdgeController(EdgeService edgeService) {
		this.edgeService = edgeService;
	}

	@GetMapping
	public ResponseEntity<List<LineResponse>> lineStations() {
		return ResponseEntity.ok(edgeService.findAll());
	}

	@PostMapping
	public ResponseEntity<EdgeResponse> create(
			@RequestBody EdgeRequest request) {
		EdgeResponse created = edgeService.create(request);
		return ResponseEntity
				.created(URI.create("/edges/" + created.getCustomId()))
				.body(created);
	}

	@DeleteMapping("/lines/{lineId}/stations/{stationId}")
	public ResponseEntity<EdgeResponse> delete(
			@PathVariable Long lineId,
			@PathVariable Long stationId) {
		return ResponseEntity.ok(edgeService.remove(lineId, stationId));
	}
}
