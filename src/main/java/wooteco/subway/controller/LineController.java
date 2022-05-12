package wooteco.subway.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.controller.dto.LineRequest;
import wooteco.subway.controller.dto.LineResponse;
import wooteco.subway.controller.dto.SectionRequest;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.service.LineService;
import wooteco.subway.service.StationService;

@RestController
@RequestMapping("/lines")
public class LineController {

	private final LineService lineService;
	private final StationService stationService;

	public LineController(LineService lineService, StationService stationService) {
		this.lineService = lineService;
		this.stationService = stationService;
	}

	@PostMapping
	public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
		Section section = toSection(lineRequest.toSectionRequest());
		Line line = lineService.create(lineRequest.getName(), lineRequest.getColor(), section);
		return ResponseEntity.created(URI.create("/lines/" + line.getId()))
			.body(LineResponse.from(line));
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<LineResponse>> showLines() {
		List<LineResponse> lineResponses = lineService.listLines().stream()
			.map(LineResponse::from)
			.collect(Collectors.toList());
		return ResponseEntity.ok().body(lineResponses);
	}

	@GetMapping("/{lineId}")
	public ResponseEntity<LineResponse> showLine(@PathVariable Long lineId) {
		Line line = lineService.findOne(lineId);
		return ResponseEntity.ok(LineResponse.from(line));
	}

	@PutMapping("/{lineId}")
	public ResponseEntity<Void> updateLine(@PathVariable Long lineId, @RequestBody LineRequest lineRequest) {
		lineService.update(lineRequest.toEntity(lineId));
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{lineId}")
	public ResponseEntity<Void> deleteLine(@PathVariable Long lineId) {
		lineService.remove(lineId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{lineId}/sections")
	public ResponseEntity<Void> createSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
		Section section = toSection(sectionRequest);
		lineService.addSection(lineId, section);
		return ResponseEntity.ok().build();
	}

	private Section toSection(SectionRequest sectionRequest) {
		Station upStation = stationService.findOne(sectionRequest.getUpStationId());
		Station downStation = stationService.findOne(sectionRequest.getDownStationId());
		return new Section(upStation, downStation, sectionRequest.getDistance());
	}

	@DeleteMapping("/{lineId}/sections")
	public ResponseEntity<Void> deleteSection(@PathVariable Long lineId, @RequestParam Long stationId) {
		lineService.deleteSection(lineId, stationId);
		return ResponseEntity.ok().build();
	}
}
