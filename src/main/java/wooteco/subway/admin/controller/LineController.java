package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

	@PostMapping()
	public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest view) {
		Line line = view.toLine();
		Line persistLine = lineService.save(line);

		return ResponseEntity
			.created(URI.create("/lines/" + persistLine.getId()))
			.body(LineResponse.of(persistLine));
	}

	@GetMapping("/{id}")
	public ResponseEntity<LineResponse> getLine(@PathVariable Long id) {
		return ResponseEntity.ok()
			.body(lineService.findLineWithStationsById(id));
	}

	@GetMapping()
	public ResponseEntity<List<LineResponse>> getLines() {
		List<Line> lines = lineService.showLines();

		return ResponseEntity.ok()
			.body(lineService.findAllLineWithStations(lines));
	}

	@PutMapping("/{id}")
	public ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineRequest view) {
		Line persistLine = lineService.updateLine(id, view.toLine());

		return ResponseEntity.ok()
			.body(LineResponse.of(persistLine));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<LineResponse> deleteLine(@PathVariable Long id) {
		lineService.deleteLineById(id);

		return ResponseEntity.noContent()
			.build();
	}

	@PutMapping("/{id}/stations")
	public ResponseEntity<LineResponse> createLineStation(@PathVariable Long id,
		@RequestBody LineStationCreateRequest lineStationCreateRequest) {
		lineService.addLineStation(id, lineStationCreateRequest.toLineStation());

		return ResponseEntity.ok()
			.body(lineService.findLineWithStationsById(id));
	}

	@DeleteMapping("/{lineId}/stations/{stationId}")
	public ResponseEntity<LineResponse> deleteLineStation(@PathVariable Long lineId, @PathVariable Long stationId) {
		lineService.removeLineStation(lineId, stationId);
		return ResponseEntity.noContent()
			.build();
	}
}