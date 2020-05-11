package wooteco.subway.admin.controller;

import java.net.URI;

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
import wooteco.subway.admin.repository.StationRepository;

@RestController
@RequestMapping("/stations")
public class StationController {
	private final StationRepository stationRepository;

	public StationController(StationRepository stationRepository) {
		this.stationRepository = stationRepository;
	}

	@GetMapping
	public ResponseEntity<Iterable<Station>> stations() {
		return ResponseEntity.ok(stationRepository.findAll());
	}

	@PostMapping
	public ResponseEntity<StationResponse> create(
			@RequestBody StationCreateRequest request) {
		Station station = request.toStation();
		Station persistStation = stationRepository.save(station);

		return ResponseEntity
				.created(URI.create("/stations/" + persistStation.getId()))
				.body(StationResponse.of(persistStation));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		stationRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
