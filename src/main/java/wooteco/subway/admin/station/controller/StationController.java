package wooteco.subway.admin.station.controller;

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

import wooteco.subway.admin.station.service.StationService;
import wooteco.subway.admin.station.service.dto.StationCreateRequest;
import wooteco.subway.admin.station.service.dto.StationResponse;

@RequestMapping("/stations")
@RestController
public class StationController {

	private final StationService stationService;

	public StationController(StationService stationService) {
		this.stationService = stationService;
	}

	@GetMapping
	public ResponseEntity<List<StationResponse>> get() {
		return ResponseEntity
			.ok()
			.body(stationService.findAll());
	}

	@PostMapping
	public ResponseEntity<StationResponse> save(@RequestBody @Valid StationCreateRequest request) {
		final StationResponse response = stationService.save(request.toStation());

		return ResponseEntity.created(URI.create("/stations/" + response.getId()))
			.body(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> remove(@PathVariable Long id) {
		stationService.delete(id);

		return ResponseEntity.noContent()
			.build();
	}

}
