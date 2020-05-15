package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.dto.controller.request.StationControllerRequest;
import wooteco.subway.admin.dto.controller.response.StationControllerResponse;
import wooteco.subway.admin.dto.service.response.StationServiceResponse;
import wooteco.subway.admin.dto.view.request.StationViewRequest;
import wooteco.subway.admin.service.StationService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("stations")
public class StationController {
	private final StationService stationService;

	public StationController(StationService stationService) {
		this.stationService = stationService;
	}

	@PostMapping
	public ResponseEntity<StationControllerResponse> createStation(@RequestBody @Valid StationViewRequest view) {
		StationServiceResponse stationResponse = stationService.save(StationControllerRequest.of(view));

		return ResponseEntity
				.created(URI.create("/stations/" + stationResponse.getId()))
				.body(StationControllerResponse.of(stationResponse));
	}

	@GetMapping
	public ResponseEntity<List<StationControllerResponse>> showStations() {
		List<StationControllerResponse> stationControllerRespons = stationService.findAll().stream()
				.map(StationControllerResponse::of)
				.collect(Collectors.toList());

		return ResponseEntity
				.ok()
				.body(stationControllerRespons);
	}

	@DeleteMapping("{id}")
	public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
		stationService.deleteBy(id);
		return ResponseEntity
				.noContent()
				.build();
	}
}
