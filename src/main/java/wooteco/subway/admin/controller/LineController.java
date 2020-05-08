package wooteco.subway.admin.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.service.LineService;

@RestController
@RequestMapping("/api/lines")
public class LineController {

	private final LineService lineService;

	public LineController(LineService lineService) {
		this.lineService = lineService;
	}

	@GetMapping("")
	public ResponseEntity<List<LineResponse>> getLines() {
		List<Line> lines = lineService.showLines();
		List<LineResponse> lineResponses = LineResponse.listOf(lines);
		return new ResponseEntity<>(lineResponses, HttpStatus.OK);
	}

	@PostMapping("")
	public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest request) {
		LineResponse lineResponse = lineService.save(request.toLine());
		return new ResponseEntity<>(lineResponse, HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity<LineResponse> findById(@PathVariable Long id) {
		LineResponse lineResponse = lineService.findLineWithStationsById(id);
		return new ResponseEntity<>(lineResponse, HttpStatus.OK);
	}

	@PutMapping("/{id}")
	public ResponseEntity<LineResponse> updateLines(@PathVariable Long id,
		@RequestBody LineRequest request) {
		LineResponse lineResponse = lineService.updateLine(id, request.toLine());
		return new ResponseEntity<>(lineResponse, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		lineService.deleteLineById(id);
		return ResponseEntity.ok().build();
	}

	// TODO stationId를 꼭 PathVariable로 넣어야 할까요?
	@PostMapping("{lineId}/stations/{stationId}")
	public ResponseEntity<Void> addStationToLine(@PathVariable Long lineId,
		@RequestBody LineStationCreateRequest request) {

		lineService.addLineStation(lineId, request);
		return ResponseEntity
			.status(HttpStatus.CREATED)
			.build();
	}

	@GetMapping("{lineId}/stations")
	public ResponseEntity<LineResponse> findStationsByLineId(@PathVariable Long lineId) {
		LineResponse lineResponse = lineService.findLineWithStationsById(lineId);
		return ResponseEntity
			.status(HttpStatus.OK)
			.body(lineResponse);
	}

	@DeleteMapping("{lineId}/stations/{stationsId}")
	public ResponseEntity<Void> deleteStationByLineId(@PathVariable Long lineId,
		@PathVariable Long stationsId) {
		lineService.removeLineStation(lineId, stationsId);
		return ResponseEntity
			.status(HttpStatus.OK)
			.build();
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<String> handler(Exception e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	}
}