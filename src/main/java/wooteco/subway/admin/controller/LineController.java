package wooteco.subway.admin.controller;

import static java.util.stream.Collectors.*;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

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
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.service.LineService;
import wooteco.subway.admin.service.StationService;

@RestController
@RequestMapping("/lines")
public class LineController {
	private final LineService lineService;
	private final StationService stationService;

	public LineController(LineService lineService, StationService stationService) {
		this.lineService = lineService;
		this.stationService = stationService;
	}

	@GetMapping
	public ResponseEntity<List<LineResponse>> findLines() {
		List<Line> lines = lineService.findAllLines();
		List<Station> allStations = stationService.findAll();
		List<LineResponse> responses = lines.stream()
			.map(line -> LineResponse.of(line, line.findContainingStationsFrom(allStations)))
			.collect(collectingAndThen(toList(), Collections::unmodifiableList));

		return ResponseEntity.ok().body(responses);
	}

	@GetMapping("/{id}")
	public ResponseEntity<LineResponse> getLine(@PathVariable Long id) {
		Line line = lineService.findLine(id);
		List<Station> stations = stationService.findAllByLineId(id);

		return ResponseEntity.ok().body(LineResponse.of(line, stations));
	}

	@PostMapping
	public ResponseEntity<LineResponse> createLines(@Valid @RequestBody LineRequest view) {
		Line savedLine = lineService.save(view.toLine());

		return ResponseEntity
			.created(URI.create("/lines/" + savedLine.getId()))
			.body(LineResponse.of(savedLine));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Void> updateLine(@PathVariable Long id, @Valid @RequestBody LineRequest view) {
		lineService.updateLine(id, view.toLine());
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
		lineService.deleteLineById(id);
		return ResponseEntity.noContent().build();
	}
}
