package wooteco.subway.ui;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.service.StationDto;
import wooteco.subway.service.StationService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class StationController {

	private final StationService stationService = new StationService(new StationDao());

	@PostMapping("/stations")
	public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
		StationDto stationDto = stationService.create(stationRequest.getName());
		return ResponseEntity.created(URI.create("/stations/" + stationDto.getId()))
			.body(new StationResponse(stationDto.getId(), stationDto.getName()));
	}

	@GetMapping(value = "/stations", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<StationResponse>> showStations() {
		List<StationResponse> stationResponses = stationService.listStations().stream()
			.map(station -> new StationResponse(station.getId(), station.getName()))
			.collect(Collectors.toList());
		return ResponseEntity.ok().body(stationResponses);
	}

	@DeleteMapping("/stations/{id}")
	public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
		stationService.remove(id);
		return ResponseEntity.noContent().build();
	}

	@ExceptionHandler
	public ResponseEntity<Void> handle(IllegalArgumentException exception) {
		return ResponseEntity.badRequest().build();
	}
}
