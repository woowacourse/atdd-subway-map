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
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.service.LineService;

@RestController
public class LineController {
	private final LineService service;

	public LineController(LineService service) {
		this.service = service;
	}

	@PostMapping("/lines")
	public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest view) {
		Line persistLine = service.save(view.toLine());

		return ResponseEntity.created(URI.create("/lines/" + persistLine.getId())).build();
	}

	@GetMapping("/lines")
	public ResponseEntity<List<LineResponse>> showLines() {
		return ResponseEntity.ok().body(service.showLines());
	}

	@GetMapping("/lines/{id}")
	public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {

		return ResponseEntity.ok().body(service.findLineWithStationsById(id));
	}

	@PutMapping("/lines/{id}")
	public ResponseEntity<Object> updateLine(@PathVariable Long id, @RequestBody LineRequest view) {
		Line line = view.toLine();
		service.updateLine(id, line);

		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/lines/{id}")
	public ResponseEntity<Object> deleteLine(@PathVariable Long id) {
		service.deleteLineById(id);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/lines/{id}/stations")
	public ResponseEntity<Object> addLineStation(@PathVariable Long id, @RequestBody LineStationRequest view) {
		service.addLineStation(id, view);

		return ResponseEntity.ok().build();
	}

	@GetMapping("/lines/{id}/stations")
	public ResponseEntity<List<StationResponse>> showLineStations(@PathVariable Long id) {
		LineResponse response = service.findLineWithStationsById(id);

		return ResponseEntity.ok().body(StationResponse.of(response.getStations()));
	}

	@DeleteMapping("/lines/{lineId}/stations/{stationId}")
	public ResponseEntity<Object> deleteLineStation(@PathVariable Long lineId, @PathVariable Long stationId) {
		service.removeLineStation(lineId, stationId);

		return ResponseEntity.noContent().build();
	}
}
