package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.request.LineStationCreateRequest;
import wooteco.subway.admin.dto.response.LineStationCreateResponse;
import wooteco.subway.admin.dto.response.StationResponse;
import wooteco.subway.admin.service.LineService;
import wooteco.subway.admin.service.StationService;

@RestController
public class LineStationController {

	private final LineService lineService;
	private final StationService stationService;

	public LineStationController(LineService lineService, StationService stationService) {
		this.lineService = lineService;
		this.stationService = stationService;
	}

	@PostMapping("/lines/{lineId}/stations")
	public ResponseEntity createLineStation(@PathVariable("lineId") Long lineId, @RequestBody LineStationCreateRequest view) {
		Station station = stationService.findBy(view.getStationId());
		lineService.addLineStation(lineId, view);
		LineStationCreateResponse lineStationCreateResponse = new LineStationCreateResponse(lineId, StationResponse.of(station));

		return ResponseEntity
				.ok()
				.body(lineStationCreateResponse);
	}

	@DeleteMapping("/lines/{lineId}/stations/{stationId}")
	public ResponseEntity deleteLineStation(@PathVariable("lineId") Long lineId, @PathVariable("stationId") Long stationId) {
		lineService.removeLineStation(lineId, stationId);

		return ResponseEntity
				.ok()
				.build();
	}
}
