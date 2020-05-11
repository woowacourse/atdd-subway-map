package wooteco.subway.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.service.StationService;

import java.net.URI;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/stations")
public class StationController {
	private final StationService stationService;

	public StationController(StationService stationService) {
		this.stationService = stationService;
	}

	@PostMapping("")
	public ResponseEntity createStation(@RequestBody StationCreateRequest request) {
		try {
			Station station = request.toStation();
			Station persistStation = stationService.save(station);
			return ResponseEntity
					.created(URI.create("/stations/" + persistStation.getId()))
					.body(StationResponse.of(station));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(null);
		}
	}

	@GetMapping("")
	public ResponseEntity showStations() {
		return ResponseEntity.ok().body(stationService.findAllStations());
	}

	@GetMapping("/names")
	public ResponseEntity showStation(@RequestParam("names") List<String> names) {
		try {
			List<Station> stationsByNames = stationService.findStationByNames(names);
			return ResponseEntity.ok().body(stationsByNames);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
					.body(Collections.EMPTY_LIST);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity deleteStation(@PathVariable Long id) {
		stationService.deleteStationById(id);
		return ResponseEntity.noContent().build();
	}
}
