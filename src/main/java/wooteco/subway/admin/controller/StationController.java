package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.dto.controller.request.StationCreateControllerRequest;
import wooteco.subway.admin.dto.controller.response.StationCreateControllerResponse;
import wooteco.subway.admin.dto.service.response.StationCreateServiceResponse;
import wooteco.subway.admin.dto.view.request.StationCreateViewRequest;
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
	public ResponseEntity<StationCreateControllerResponse> createStation(@RequestBody @Valid StationCreateViewRequest view) {
		StationCreateServiceResponse stationResponse = stationService.save(StationCreateControllerRequest.of(view));

		return ResponseEntity
				.created(URI.create("/stations/" + stationResponse.getId()))
				.body(StationCreateControllerResponse.of(stationResponse));
	}

	@GetMapping
	public ResponseEntity<List<StationCreateControllerResponse>> showStations() {
		List<StationCreateControllerResponse> stationCreateControllerResponses = stationService.findAll().stream()
				.map(StationCreateControllerResponse::of)
				.collect(Collectors.toList());

		return ResponseEntity
				.ok()
				.body(stationCreateControllerResponses);
	}

	@DeleteMapping("{id}")
	public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
		stationService.deleteBy(id);
		return ResponseEntity
				.noContent()
				.build();
	}
}
