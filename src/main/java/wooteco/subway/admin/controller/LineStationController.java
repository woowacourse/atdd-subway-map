package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.LineStationCreateResponse;
import wooteco.subway.admin.dto.LineStationRequest;
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

	@PostMapping("/lines/stations")
	public ResponseEntity createLineStation(@RequestBody LineStationRequest view) {
		Station station = stationService.findBy(view.getStationId());
		if (isRequestForFirstStation(view)) {
			lineService.addLineStation(view.getLineId(),
					new LineStationCreateRequest(null, station.getId(), 0, 0));

			LineStationCreateResponse lineStationCreateResponse = new LineStationCreateResponse(view.getLineId(), StationResponse.of(station));

			return ResponseEntity
					.ok()
					.body(lineStationCreateResponse);
		}

		Station preStation = stationService.findBy(view.getPreStationId());

		lineService.addLineStation(view.getLineId(),
				new LineStationCreateRequest(preStation.getId(), station.getId(), 0, 0));

		LineStationCreateResponse lineStationCreateResponse = new LineStationCreateResponse(view.getLineId(), StationResponse.of(station));

		return ResponseEntity
				.ok()
				.body(lineStationCreateResponse);
	}

	private boolean isRequestForFirstStation(@RequestBody LineStationRequest view) {
		return view.getPreStationId() == null;
	}

	@DeleteMapping("/lines/{lineId}/stations/{stationId}")
	public ResponseEntity deleteLineStation(@PathVariable("lineId") Long lineId, @PathVariable("stationId") Long stationId) {

		lineService.removeLineStation(lineId, stationId);

		return ResponseEntity
				.ok()
				.build();
	}
}
