package wooteco.subway.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.controller.dto.StationRequest;
import wooteco.subway.controller.dto.StationResponse;
import wooteco.subway.domain.Station;
import wooteco.subway.service.StationService;

@RestController
@RequestMapping("/stations")
public class StationController {

	private final StationService stationService;

	public StationController(StationService stationService) {
		this.stationService = stationService;
	}

	@PostMapping
	public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
		Station station = stationService.create(stationRequest.getName());
		return ResponseEntity.created(URI.create("/stations/" + station.getId()))
			.body(StationResponse.from(station));
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<StationResponse>> showStations() {
		List<StationResponse> stationResponses = stationService.findAllStations().stream()
			.map(StationResponse::from)
			.collect(Collectors.toList());
		return ResponseEntity.ok().body(stationResponses);
	}

	@DeleteMapping("/{stationId}")
	public ResponseEntity<Void> deleteStation(@PathVariable Long stationId) {
		stationService.remove(stationId);
		return ResponseEntity.noContent().build();
	}
}
