package wooteco.subway.admin.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.service.StationService;

@RestController
public class StationController {
	private final StationService stationService;

	public StationController(final StationService stationService) {
		this.stationService = stationService;
	}

	@PostMapping("/stations")
	public ResponseEntity createStation(@RequestBody StationCreateRequest view) {
		Station station = view.toStation();
		StationResponse stationResponse = stationService.createStation(station);

		return ResponseEntity
			.created(URI.create("/stations/" + stationResponse.getId()))
			.body(stationResponse);
	}

	@GetMapping("/stations")
	public ResponseEntity showStations() {
		return ResponseEntity.ok().body(stationService.showStations());
	}

	@DeleteMapping("/stations/{id}")
	public ResponseEntity deleteStation(@PathVariable Long id) {
		stationService.deleteStation(id);
		return ResponseEntity.noContent().build();
	}
}
