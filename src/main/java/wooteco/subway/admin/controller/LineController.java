package wooteco.subway.admin.controller;

import java.net.URI;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.service.LineService;

@RestController
public class LineController {
	// private LineService service;
	//
	// public LineController(LineService service) {
	// 	this.service = service;
	// }


	private List<Line> lines;
	private List<Station> stations;

	@Autowired
	public LineController() {
		this.lines = new ArrayList<>();
		this.stations = new ArrayList<>();
		lines.add(new Line(1L, "2호선", LocalTime.of(5, 30), LocalTime.of(23, 30), 10));
		stations.add(new Station(0L, "잠실"));
		stations.add(new Station(1L, "잠실나루"));
	}

	// @PostMapping("/lines")
	// public ResponseEntity createLine(@RequestBody LineRequest view) {
	// 	Line persistLine = service.save(view.toLine());
	//
	// 	return ResponseEntity.created(URI.create("/lines/" + persistLine.getId()))
	// 		.body(LineResponse.of(persistLine));
	// }
	//
	// @GetMapping("/lines")
	// public ResponseEntity showLines() {
	// 	return ResponseEntity.ok().body(LineResponse.listOf(service.showLines()));
	// }
	//
	// @GetMapping("/lines/{id}")
	// public ResponseEntity showLine(@PathVariable Long id) {
	// 	return ResponseEntity.ok().body(LineResponse.of(service.showLine(id)));
	// }
	//
	// @PutMapping("/lines/{id}")
	// public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest view) {
	// 	Line line = view.toLine();
	// 	service.updateLine(id, line);
	//
	// 	return ResponseEntity.ok().body(LineResponse.of(line));
	// }
	//
	// @DeleteMapping("/lines/{id}")
	// public ResponseEntity deleteLine(@PathVariable Long id) {
	// 	service.deleteLineById(id);
	// 	return ResponseEntity.noContent().build();
	// }


	@GetMapping("/lines/{id}")
	public ResponseEntity showLine(@PathVariable Long id) {
		Line line = lines.stream()
			.filter(x -> x.getId().equals(id))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("없"));

		Set<Station> stationsOfLine = line.getStations()
			.stream()
			.map(lineStation -> this.stations.get((lineStation.getStationId()).intValue()))
			.collect(Collectors.toSet());

		LineResponse lineResponse = new LineResponse(line.getId(), line.getTitle(), line.getStartTime(),
			line.getEndTime(),
			line.getIntervalTime(), line.getBgColor(), line.getCreatedAt(), line.getUpdatedAt(), stationsOfLine);

		return ResponseEntity.ok().body(lineResponse);
	}

	@PutMapping("/lines/{id}/stations")
	public ResponseEntity addLineStation(@PathVariable Long id, @RequestBody LineStationCreateRequest view) {
		Line line = lines.stream()
			.filter(x -> x.getId().equals(id))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("없"));

		line.addLineStation(view.toLineStation(id));

		Set<Station> stationsOfLine = line.getStations()
			.stream()
			.map(lineStation -> this.stations.get((lineStation.getStationId()).intValue()))
			.collect(Collectors.toSet());

		LineResponse lineResponse = new LineResponse(line.getId(), line.getTitle(), line.getStartTime(),
			line.getEndTime(),
			line.getIntervalTime(), line.getBgColor(), line.getCreatedAt(), line.getUpdatedAt(), stationsOfLine);

		return ResponseEntity.ok().body(lineResponse);
	}

	@GetMapping("/lines/{id}/stations")
	public ResponseEntity showLineStations(@PathVariable Long id) {
		Line line = lines.stream()
			.filter(x -> x.getId().equals(id))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("없"));

		Set<Station> stationsOfLine = line.getStations()
			.stream()
			.map(lineStation -> this.stations.get((lineStation.getStationId()).intValue()))
			.collect(Collectors.toSet());

		return ResponseEntity.ok().body(StationResponse.of(stationsOfLine));
	}

	@DeleteMapping("/lines/{lineId}/stations/{stationId}")
	public ResponseEntity deleteLineStation(@PathVariable Long lineId, @PathVariable Long stationId) {
		Line line = lines.stream()
			.filter(x -> x.getId().equals(lineId))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("없"));
		line.removeLineStationById(stationId);

		return ResponseEntity.noContent().build();
	}
}
