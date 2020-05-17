package wooteco.subway.admin.line.controller.edge;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.line.service.LineService;
import wooteco.subway.admin.line.service.dto.edge.LineStationCreateRequest;
import wooteco.subway.admin.line.service.dto.line.LineResponse;
import wooteco.subway.admin.station.service.dto.StationResponse;

@RequestMapping("/lines")
@RestController
public class LineStationController {

	private final LineService lineService;

	public LineStationController(LineService lineService) {
		this.lineService = lineService;
	}

	@GetMapping("/{lineId}/stations")
	public ResponseEntity<List<StationResponse>> get(@PathVariable Long lineId) {
		LineResponse line = lineService.findLineWithStationsById(lineId);

		return ResponseEntity
			.ok()
			.body(line.getStations());
	}

	@PutMapping("/{lineId}/stations")
	public ResponseEntity<LineResponse> save(@PathVariable Long lineId,
		@RequestBody @Valid LineStationCreateRequest request) {
		lineService.save(lineId, request);

		return ResponseEntity
			.noContent()
			.build();
	}

	@DeleteMapping("/{lineId}/stations/{stationId}")
	public ResponseEntity<Void> deleteLineStation(@PathVariable Long lineId,
		@PathVariable Long stationId) {
		lineService.delete(lineId, stationId);

		return ResponseEntity
			.noContent()
			.build();
	}

}
