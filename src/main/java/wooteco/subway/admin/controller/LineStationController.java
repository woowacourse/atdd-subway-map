package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.LineStationRequest;
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
		if (view.getPreStationName().isEmpty()) {
			Station station = stationService.findByName(view.getStationName());

			lineService.addLineStation(view.getLineId(),
					new LineStationCreateRequest(null, station.getId(), 0, 0));

			return ResponseEntity
					.ok()
					.build();
		}
		Station preStation = stationService.findByName(view.getPreStationName());
		Station station = stationService.findByName(view.getStationName());

		lineService.addLineStation(view.getLineId(),
				new LineStationCreateRequest(preStation.getId(), station.getId(), 0, 0));

		return ResponseEntity
				.ok()
				.build();
//				.body(LineStationResponse.of(view.toLineStation()));
	}

	@DeleteMapping("/lines/{lineId}/stations/{stationId}")
	public ResponseEntity deleteLineStation(@PathVariable("lineId") Long lineId, @PathVariable("stationId") Long stationId) {

		lineService.removeLineStation(lineId, stationId);

		return ResponseEntity
				.ok()
				.build();
	}
}
