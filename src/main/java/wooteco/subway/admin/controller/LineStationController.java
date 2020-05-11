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

import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.LineStationResponse;
import wooteco.subway.admin.service.LineService;

@RestController
@RequestMapping("/lines/{lineId}/stations")
public class LineStationController {
	private final LineService lineService;

	public LineStationController(LineService lineService) {
		this.lineService = lineService;
	}

	@PostMapping
	public ResponseEntity<LineStationResponse> createLineStation(@PathVariable Long lineId,
		@Valid @RequestBody LineStationCreateRequest view) {
		LineStation lineStation = view.toLineStation();
		lineService.addLineStation(lineId, lineStation);

		return ResponseEntity
			.created(URI.create("/lines/" + lineId + "/stations/" + lineStation.getStationId()))
			.body(LineStationResponse.of(lineId, lineStation));
	}

	@GetMapping
	public ResponseEntity<List<LineStationResponse>> getLineStations(@PathVariable Long lineId) {
		List<LineStation> lineStations = lineService.findLineStations(lineId);
		return ResponseEntity.ok().body(LineStationResponse.ofList(lineId, lineStations));
	}

	@DeleteMapping("/{stationId}")
	public ResponseEntity<Void> deleteLine(@PathVariable Long lineId, @PathVariable Long stationId) {
		lineService.removeLineStation(lineId, stationId);
		return ResponseEntity.noContent().build();
	}
}
