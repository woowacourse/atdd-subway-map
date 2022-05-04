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

import wooteco.subway.controller.dto.ControllerDtoAssembler;
import wooteco.subway.controller.dto.line.LineRequest;
import wooteco.subway.controller.dto.line.LineResponse;
import wooteco.subway.service.LineService;

@RestController
@RequestMapping("/lines")
public class LineController {

	private final LineService lineService;

	public LineController(LineService lineService) {
		this.lineService = lineService;
	}

	@PostMapping
	public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
		LineResponse lineResponse = ControllerDtoAssembler.lineResponse(
				lineService.create(lineRequest.getName(), lineRequest.getColor()));
		return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<LineResponse>> showLines() {
		List<LineResponse> lineResponses = lineService.listLines().stream()
			.map(ControllerDtoAssembler::lineResponse)
			.collect(Collectors.toList());
		return ResponseEntity.ok().body(lineResponses);
	}

	@GetMapping("/{lineId}")
	public ResponseEntity<LineResponse> showLine(@PathVariable Long lineId) {
		return ResponseEntity.ok(ControllerDtoAssembler.lineResponse(lineService.findOne(lineId)));
	}

	@PutMapping("/{lineId}")
	public ResponseEntity<Void> updateLine(@PathVariable Long lineId, @RequestBody LineRequest lineRequest) {
		lineService.update(lineId, ControllerDtoAssembler.lineRequestDto(lineRequest));
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{lineId}")
	public ResponseEntity<Void> deleteLine(@PathVariable Long lineId) {
		lineService.remove(lineId);
		return ResponseEntity.noContent().build();
	}

	@ExceptionHandler
	public ResponseEntity<String> handle(IllegalArgumentException exception) {
		return ResponseEntity.badRequest().body(exception.getMessage());
	}
}
