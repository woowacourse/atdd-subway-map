package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.dto.request.StationCreateRequest;
import wooteco.subway.admin.dto.response.StationResponse;
import wooteco.subway.admin.service.StationService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("stations")
public class StationController {
	private final StationService stationService;

	public StationController(StationService stationService) {
		this.stationService = stationService;
	}

	@PostMapping
	public ResponseEntity<StationResponse> createStation(@RequestBody StationCreateRequest view) {
		StationResponse stationResponse = stationService.save(view);

		return ResponseEntity
				.created(URI.create("/stations/" + stationResponse.getId()))
				.body(stationResponse);
	}

	@GetMapping
	public ResponseEntity<List<StationResponse>> showStations() {
		return ResponseEntity
				.ok()
				.body(stationService.findAll());
	}

	@DeleteMapping("{id}")
	public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
		stationService.deleteBy(id);
		return ResponseEntity
				.noContent()
				.build();
	}
}
