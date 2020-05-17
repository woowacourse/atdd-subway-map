package wooteco.subway.admin.controller;

import static wooteco.subway.admin.controller.DefinedSqlException.DUPLICATED_NAME;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.domain.station.Station;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.repository.StationRepository;

@RestController
public class StationController {
	private final StationRepository stationRepository;

	public StationController(StationRepository stationRepository) {
		this.stationRepository = stationRepository;
	}

	@PostMapping("/stations")
	public ResponseEntity<String> createStation(@RequestBody @Valid StationCreateRequest view) {
	Station station = view.toStation();
	Long stationId;
		try {
		stationId = stationRepository.save(station).getId();
	} catch (DuplicateKeyException e) {
		throw new DefinedSqlException(DUPLICATED_NAME);
	}

		return ResponseEntity
			.created(URI.create("/stations/" + stationId))
			.build();
	}

	@GetMapping("/stations")
	public ResponseEntity<List<StationResponse>> showStations() {
		List<StationResponse> responses = StationResponse.listOf(stationRepository.findAll());
		return ResponseEntity.ok().body(responses);
	}

	@DeleteMapping("/stations/{id}")
	public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
		stationRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
