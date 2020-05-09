package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.LineStationRequest;
import wooteco.subway.admin.dto.LineStationResponse;
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

	@PostMapping("/line-stations")
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

	@DeleteMapping("/line-stations")
	public ResponseEntity deleteLineStation(@RequestBody LineStationRequest view) {
		return ResponseEntity
				.ok()
				.build();
	}
}
