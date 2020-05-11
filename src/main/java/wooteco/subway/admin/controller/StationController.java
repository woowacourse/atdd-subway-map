package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.service.StationService;

@RestController
@RequestMapping("/stations")
public class StationController {
	private final StationService stationService;

	public StationController(StationService stationService) {
		this.stationService = stationService;
	}

	@PostMapping
	public ResponseEntity<StationResponse> createStation(@Valid @RequestBody StationCreateRequest view) {
		Station savedStation = stationService.create(view.toStation());

		return ResponseEntity
			.created(URI.create("/stations/" + savedStation.getId()))
			.body(StationResponse.of(savedStation));
	}

	@GetMapping
	public ResponseEntity<List<StationResponse>> showStations() {
		return ResponseEntity.ok().body(StationResponse.ofList(stationService.findAll()));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
		stationService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
