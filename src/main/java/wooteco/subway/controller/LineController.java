package wooteco.subway.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
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

import wooteco.subway.controller.dto.LineRequest;
import wooteco.subway.controller.dto.LineResponse;
import wooteco.subway.dao.LineDao;
import wooteco.subway.service.LineService;
import wooteco.subway.service.dto.LineDto;

@RestController
@RequestMapping("/lines")
public class LineController {

	private final LineService lineService = new LineService(new LineDao());

	@PostMapping
	public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
		LineDto lineDto = lineService.create(lineRequest.getName(), lineRequest.getColor());
		return ResponseEntity.created(URI.create("/lines/" + lineDto.getId()))
			.body(new LineResponse(lineDto.getId(), lineDto.getName(), lineDto.getColor()));
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<LineResponse>> showLines() {
		List<LineResponse> lineResponses = lineService.listLines().stream()
			.map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
			.collect(Collectors.toList());
		return ResponseEntity.ok().body(lineResponses);
	}

	@GetMapping("/{lineId}")
	public ResponseEntity<LineResponse> showLine(@PathVariable Long lineId) {
		LineDto line = lineService.findOne(lineId);
		return ResponseEntity.ok(new LineResponse(line.getId(), line.getName(), line.getColor()));
	}

	@PutMapping("/{lineId}")
	public ResponseEntity<Void> updateLine(@PathVariable Long lineId, @RequestBody LineRequest lineRequest) {
		lineService.update(lineId, lineRequest.getName(), lineRequest.getColor());
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{lineId}")
	public ResponseEntity<Void> deleteLine(@PathVariable Long lineId) {
		lineService.remove(lineId);
		return ResponseEntity.noContent().build();
	}

	@ExceptionHandler
	public ResponseEntity<Void> handle(IllegalArgumentException exception) {
		return ResponseEntity.badRequest().build();
	}
}
