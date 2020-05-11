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

import wooteco.subway.admin.dto.StationRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.service.StationService;

@RestController
@RequestMapping("/stations")
public class StationController {
	private final StationService stationService;

	public StationController(StationService stationService) {
		this.stationService = stationService;
	}

	@GetMapping
	public ResponseEntity<List<StationResponse>> stations() {
		return ResponseEntity.ok(stationService.findAll());
	}

	@PostMapping
	public ResponseEntity<StationResponse> create(
			@RequestBody StationRequest request) {
		StationResponse created = stationService.create(request);
		return ResponseEntity
				.created(URI.create("/stations/" + created.getId()))
				.body(created);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		stationService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
