package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.dto.LineStationRequest;
import wooteco.subway.admin.dto.LineStationResponse;

@RestController
public class LineStationController {

	@PostMapping("/line-stations")
	public ResponseEntity createLineStation(@RequestBody LineStationRequest view) {
		return ResponseEntity
				.ok()
				.body(LineStationResponse.of(view.toLineStation()));
	}

	@DeleteMapping("/line-stations")
	public ResponseEntity deleteLineStation(@RequestParam Long lineId, @RequestParam Long stationId) {
		return ResponseEntity
				.ok()
				.build();
	}
}
