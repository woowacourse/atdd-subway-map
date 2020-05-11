package wooteco.subway.admin.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

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
import wooteco.subway.admin.repository.LineRepository;

@RestController
@RequestMapping("/lines")
public class LineController {
	private final LineRepository lineRepository;

	public LineController(LineRepository lineRepository) {
		this.lineRepository = lineRepository;
	}

	@GetMapping
	public ResponseEntity<List<LineResponse>> lines() {
		return ResponseEntity.ok(LineResponse.listOf(lineRepository.findAll()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<LineResponse> line(
			@PathVariable("id") Long id) {
		return ResponseEntity.ok()
				.body(LineResponse.of(lineRepository.findById(id)
						.orElseThrow(NoSuchElementException::new)));
	}

	@PostMapping
	public ResponseEntity<Void> create(
			@RequestBody LineRequest request) throws URISyntaxException {
		String name = request.getName();
		String color = request.getColor();
		LocalTime startTime = request.getStartTime();
		LocalTime endTime = request.getEndTime();
		int intervalTime = request.getIntervalTime();

		Line line = new Line(name, color, startTime, endTime, intervalTime);
		Line created = lineRepository.save(line);

		URI url = new URI("/lines/" + created.getId());
		return ResponseEntity.created(url).build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<Line> update(
			@PathVariable("id") Long id, @RequestBody LineRequest request) {
		String name = request.getName();
		String color = request.getColor();
		LocalTime startTime = request.getStartTime();
		LocalTime endTime = request.getEndTime();
		int intervalTime = request.getIntervalTime();

		Line line = lineRepository.findById(id)
				.orElseThrow(NoSuchElementException::new);
		line.update(new Line(name, color, startTime, endTime, intervalTime));

		Line updated = lineRepository.save(line);
		return ResponseEntity.ok(updated);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(
			@PathVariable Long id) {
		lineRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
