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
import wooteco.subway.admin.repository.StationRepository;

@RestController
public class StationController {
	private final StationRepository stationRepository;

	public StationController(StationRepository stationRepository) {
		this.stationRepository = stationRepository;
	}

	@GetMapping("/stations")
	public ResponseEntity<?> showStations() {
		return ResponseEntity.ok().body(stationRepository.findAll());
	}

	@PostMapping("/stations")
	public ResponseEntity<?> createStation(@RequestBody StationCreateRequest request) {
		Station station = request.toStation();
		Station persistStation = stationRepository.save(station);

		return ResponseEntity
				.created(URI.create("/stations/" + persistStation.getId()))
				.body(StationResponse.of(persistStation));
	}

	@DeleteMapping("/stations/{id}")
	public ResponseEntity<?> deleteStation(@PathVariable Long id) {
		stationRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
