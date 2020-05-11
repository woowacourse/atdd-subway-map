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
import wooteco.subway.admin.dto.Request;
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
	public ResponseEntity<StationResponse> createStation(@RequestBody Request<StationCreateRequest> view) {
		StationCreateRequest content = view.getContent();
		Station entity = content.toStation();
		if (stationRepository.existsByName(entity.getName())) {
			throw new IllegalArgumentException("이미 저장되어있는 역이름입니다.");
		}
		Station persistStation = stationRepository.save(entity);

		return ResponseEntity
			.created(URI.create("/stations/" + persistStation.getId()))
			.body(StationResponse.of(persistStation));
	}

	@GetMapping
	public ResponseEntity<List<StationResponse>> showStations() {
		return ResponseEntity.ok().body(StationResponse.ofList(stationRepository.findAll()));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
		if (!stationRepository.existsById(id)) {
			throw new IllegalArgumentException("존재하지 않는 리소스는 이용할 수 없습니다.");
		}
		stationRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
