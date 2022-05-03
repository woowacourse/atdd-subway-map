package wooteco.subway.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import wooteco.subway.controller.dto.StationRequest;
import wooteco.subway.controller.dto.StationResponse;
import wooteco.subway.service.dto.StationDto;
import wooteco.subway.service.StationService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stations")
public class StationController {

	private final StationService stationService;

	public StationController(StationService stationService) {
		this.stationService = stationService;
	}

	@PostMapping
	public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
		StationDto stationDto = stationService.create(stationRequest.getName());
		return ResponseEntity.created(URI.create("/stations/" + stationDto.getId()))
			.body(new StationResponse(stationDto.getId(), stationDto.getName()));
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<StationResponse>> showStations() {
		List<StationResponse> stationResponses = stationService.listStations().stream()
			.map(station -> new StationResponse(station.getId(), station.getName()))
			.collect(Collectors.toList());
		return ResponseEntity.ok().body(stationResponses);
	}

	@DeleteMapping("/{stationId}")
	public ResponseEntity<Void> deleteStation(@PathVariable Long stationId) {
		stationService.remove(stationId);
		return ResponseEntity.noContent().build();
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Void> handle() {
		return ResponseEntity.badRequest().build();
	}
}
