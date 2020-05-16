package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.service.StationService;

@RestController
public class StationController {
	private final StationService stationService;

	public StationController(StationService stationService) {
		this.stationService = stationService;
	}

	@PostMapping("/stations")
	public ResponseEntity createStation(@RequestBody StationCreateRequest stationCreateRequest) {
		StationResponse stationResponse = stationService.createStation(stationCreateRequest);

		return ResponseEntity
			.created(URI.create("/stations/" + stationResponse.getId()))
			.body(stationResponse);
	}

	@GetMapping("/stations")
	public ResponseEntity showStations() {
		List<StationResponse> stationResponses = stationService.showStations();
		return ResponseEntity
			.ok()
			.body(stationResponses);
	}

	@DeleteMapping("/stations/{id}")
	public ResponseEntity deleteStation(@PathVariable Long id) {
		stationService.deleteStations(id);
		return ResponseEntity.noContent().build();
	}
}
