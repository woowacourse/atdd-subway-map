package wooteco.subway.admin.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/stations")
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
			.status(HttpStatus.CREATED)
			.body(StationResponse.of(persistStation));
	}

	@GetMapping
	public ResponseEntity<Iterable<Station>> showStations() {
		System.out.println(stationRepository.findAll());
		List<Station> stations = stationRepository.findAll();
		StationResponse.listOf(stations);
		return ResponseEntity.ok().body(stations);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
		stationRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
