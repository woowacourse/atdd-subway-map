package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.List;

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

	@PostMapping
	public ResponseEntity<StationResponse> createStation(@RequestBody StationCreateRequest view) {
		Station station = view.toStation();
		Station persistStation = stationRepository.save(station);

		return ResponseEntity
			.created(URI.create("/stations/" + persistStation.getId()))
			.body(StationResponse.of(persistStation));
	}

	@GetMapping
	public ResponseEntity<List<Station>> showStations() {
		return ResponseEntity.ok().body(stationRepository.findAll());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
		stationRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
