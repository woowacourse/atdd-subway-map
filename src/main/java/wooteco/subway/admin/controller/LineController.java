package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.dto.controller.request.LineControllerRequest;
import wooteco.subway.admin.dto.controller.response.LineControllerResponse;
import wooteco.subway.admin.dto.controller.response.LineWithStationsControllerResponse;
import wooteco.subway.admin.dto.service.response.LineServiceResponse;
import wooteco.subway.admin.dto.service.response.LineWithStationsServiceResponse;
import wooteco.subway.admin.dto.view.request.LineViewRequest;
import wooteco.subway.admin.service.LineService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("lines")
public class LineController {
	private final LineService lineService;

	public LineController(LineService lineService) {
		this.lineService = lineService;
	}

	@GetMapping
	public ResponseEntity<List<LineWithStationsControllerResponse>> showLines() {
		List<LineWithStationsControllerResponse> lineWithStationsControllerResponses = lineService.findLines().stream()
				.map(LineWithStationsControllerResponse::of)
				.collect(Collectors.toList());

		return ResponseEntity
				.ok()
				.body(lineWithStationsControllerResponses);
	}

	@GetMapping("{id}")
	public ResponseEntity<LineWithStationsControllerResponse> findLineWithStationsBy(@PathVariable(name = "id") Long id) {
		LineWithStationsServiceResponse lineWithStationsResponse = lineService.findLineWithStationsBy(id);

		return ResponseEntity
				.ok()
				.body(LineWithStationsControllerResponse.of(lineWithStationsResponse));
	}

	@PostMapping
	public ResponseEntity<LineControllerResponse> createLine(@RequestBody @Valid LineViewRequest view) {
		LineServiceResponse lineServiceResponse = lineService.save(LineControllerRequest.of(view));

		return ResponseEntity
				.created(URI.create("/lines/" + lineServiceResponse.getId()))
				.body(LineControllerResponse.of(lineServiceResponse));
	}

	@PutMapping("{id}")
	public ResponseEntity<Void> updateLineBy(@PathVariable(name = "id") Long id, @RequestBody @Valid LineViewRequest view) {
		lineService.updateLine(id, LineControllerRequest.of(view));

		return ResponseEntity
				.noContent()
				.build();
	}

	@DeleteMapping("{id}")
	public ResponseEntity<Void> deleteLineBy(@PathVariable(name = "id") Long id) {
		lineService.deleteLineBy(id);

		return ResponseEntity
				.noContent()
				.build();
	}
}
