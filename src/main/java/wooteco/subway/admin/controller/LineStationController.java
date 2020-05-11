package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.dto.request.LineStationCreateRequest;
import wooteco.subway.admin.dto.response.LineStationCreateResponse;
import wooteco.subway.admin.dto.response.StationResponse;
import wooteco.subway.admin.service.LineService;
import wooteco.subway.admin.service.StationService;

@RestController
@RequestMapping("lines")
public class LineStationController {

	private final LineService lineService;
	private final StationService stationService;

	public LineStationController(LineService lineService, StationService stationService) {
		this.lineService = lineService;
		this.stationService = stationService;
	}

	@PostMapping("{lineId}/stations")
	public ResponseEntity<LineStationCreateResponse> createLineStation(@PathVariable("lineId") Long lineId, @RequestBody LineStationCreateRequest view) {
		lineService.addLineStation(lineId, view);
		StationResponse stationResponse = stationService.findBy(view.getStationId());
		LineStationCreateResponse lineStationCreateResponse = new LineStationCreateResponse(lineId, stationResponse);

		return ResponseEntity
				.ok()
				.body(lineStationCreateResponse);
	}

	@DeleteMapping("{lineId}/stations/{stationId}")
	public ResponseEntity<Void> deleteLineStation(@PathVariable("lineId") Long lineId, @PathVariable("stationId") Long stationId) {
		lineService.removeLineStation(lineId, stationId);

		return ResponseEntity
				.noContent()
				.build();
	}
}
