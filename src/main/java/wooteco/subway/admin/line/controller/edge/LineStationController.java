package wooteco.subway.admin.line.controller.edge;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.line.service.LineService;
import wooteco.subway.admin.line.service.dto.edge.LineStationCreateRequest;
import wooteco.subway.admin.line.service.dto.line.LineResponse;

@RequestMapping("/lines")
@RestController
public class LineStationController {

	private final LineService lineService;

	public LineStationController(LineService lineService) {
		this.lineService = lineService;
	}

	@PutMapping("/{lineId}/stations")
	public ResponseEntity<LineResponse> save(@PathVariable Long lineId,
		@RequestBody @Valid LineStationCreateRequest request) {
		lineService.save(lineId, request);
		return ResponseEntity.noContent()
		                     .build();
	}

	@DeleteMapping("/{lineId}/stations/{stationId}")
	public ResponseEntity<Void> deleteLineStation(@PathVariable Long lineId,
		@PathVariable Long stationId) {
		lineService.delete(lineId, stationId);
		return ResponseEntity.noContent()
		                     .build();
	}

}
