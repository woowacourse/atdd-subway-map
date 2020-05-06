package wooteco.subway.admin.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
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

@RestController
@RequestMapping("/lines")
public class MockLineController {

	private Map<Long, Line> lines = new HashMap<>();

	@GetMapping("")
	public ResponseEntity<List<LineResponse>> getLines() {
		List<LineResponse> lineResponses = new ArrayList<>();
		for (Long id : lines.keySet()) {
			Line line = lines.get(id);
			lineResponses.add(new LineResponse(id, line.getName(),
				line.getStartTime(), line.getEndTime(), line.getIntervalTime(), line.getCreatedAt()
				, line.getUpdatedAt(), new HashSet<>()));
		}
		return new ResponseEntity<>(lineResponses, HttpStatus.OK);
	}

	@PostMapping("")
	public ResponseEntity<Void> createLine(@RequestBody LineRequest request) {
		Line line = new Line(request.getName(), request.getStartTime(), request.getEndTime(),
			request.getIntervalTime());
		lines.put((long) lines.size() + 1, line);
		return ResponseEntity
			.status(HttpStatus.CREATED)
			.build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<LineResponse> findById(@PathVariable Long id) {
		Line line = lines.get(id);
		LineResponse lineResponse = new LineResponse(id, line.getName(),
			line.getStartTime(), line.getEndTime(), line.getIntervalTime(), line.getCreatedAt()
			, line.getUpdatedAt(), new HashSet<>());
		return new ResponseEntity<>(lineResponse, HttpStatus.OK);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Void> updateLines(@PathVariable Long id,
		@RequestBody LineRequest request) {

		Line line = lines.get(id);
		Line dummyLine = new Line(line.getName(), request.getStartTime(), request.getEndTime(),
			request.getIntervalTime());

		line.update(dummyLine);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		lines.remove(id);
		return ResponseEntity.ok().build();
	}
}
