package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.ErrorResponse;
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
	public ResponseEntity<StationResponse> createStation(@RequestBody StationCreateRequest view) {
		Station station = view.toStation();
		StationResponse stationResponse = stationService.createStation(station);

		return ResponseEntity
			.created(URI.create("/stations/" + stationResponse.getId()))
			.body(stationResponse);
	}

	@GetMapping("/stations")
	public ResponseEntity<List<StationResponse>> showStations() {
		return ResponseEntity.ok().body(stationService.showStations());
	}

	@DeleteMapping("/stations/{id}")
	public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
		stationService.deleteStation(id);
		return ResponseEntity.noContent().build();
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleException(IllegalArgumentException e) {
		return ResponseEntity.badRequest().body(ErrorResponse.of(e.getMessage()));
	}
}
