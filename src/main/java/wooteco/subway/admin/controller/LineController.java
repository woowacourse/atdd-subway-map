package wooteco.subway.admin.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.repository.LineRepository;

@Controller
public class LineController {
	private final LineRepository lineRepository;

	public LineController(LineRepository lineRepository) {
		this.lineRepository = lineRepository;
	}

	@PostMapping("/lines")
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

	@GetMapping("/lines")
	public ResponseEntity<List<LineResponse>> lines() {
		return ResponseEntity.ok(LineResponse.listOf(lineRepository.findAll()));
	}

	@GetMapping("/lines/{id}")
	public ResponseEntity<LineResponse> line(
			@PathVariable("id") Long id) {
		return ResponseEntity.ok()
				.body(LineResponse.of(lineRepository.findById(id)
						.orElseThrow(NoSuchElementException::new)));
	}

	@PutMapping("/lines/{id}")
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

	@DeleteMapping("/lines/{id}")
	public ResponseEntity<Void> delete(
			@PathVariable Long id) {
		lineRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
