package wooteco.subway.admin.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.dto.LineStationRequest;
import wooteco.subway.admin.service.LineService;
import wooteco.subway.admin.service.LineStationService;

@RestController
public class LineStationController {
	private final LineStationService lineStationService;

	public LineStationController(LineStationService lineStationService) {
		this.lineStationService = lineStationService;
	}

	@GetMapping("/admin-edge")
	public ModelAndView adminEdge() {
		return new ModelAndView("admin-edge");
	}

	@GetMapping("/lineStations")
	public ResponseEntity<List<?>> lineStations() {
		return ResponseEntity.ok(lineStationService.findAll());
	}

	@PostMapping("/lineStation")
	public ResponseEntity<?> create(
			@RequestBody LineStationRequest request) throws URISyntaxException {
		String lineName = request.getLineName();
		String preStationName = request.getPreStationName();
		String stationName = request.getStationName();
		int distance = request.getDistance();
		int duration = request.getDuration();

		LineStation lineStation = lineStationService.createLineStation(
				lineName, preStationName, stationName, distance, duration);

		URI url = new URI("/lineStation/" + lineStation.getCustomId());
		return ResponseEntity.created(url).body(lineStation);
	}
}
