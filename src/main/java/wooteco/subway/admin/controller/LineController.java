package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.admin.dto.LineCreateRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineUpdateRequest;
import wooteco.subway.admin.service.LineService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("lines")
public class LineController {

    private final LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<Long> createLine(@RequestBody @Valid LineCreateRequest lineCreateRequest) {
        Long id = lineService.save(lineCreateRequest.toLine());
        return ResponseEntity.created(URI.create("/lines/" + id)).body(id);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> getLines() {
        return ResponseEntity.ok(lineService.getLineResponses());
    }

    @GetMapping("{id}")
    public ResponseEntity<LineResponse> getLine(@PathVariable("id") Long lineId) {
        return ResponseEntity.ok(lineService.findLineWithStationsById(lineId));
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> updateLine(@PathVariable("id") Long lineId,
                                           @RequestBody LineUpdateRequest lineUpdateRequest) {
        lineService.updateLine(lineId, lineUpdateRequest.toLine());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable("id") Long lineId) {
        lineService.deleteLineById(lineId);
        return ResponseEntity.ok().build();
    }
}
