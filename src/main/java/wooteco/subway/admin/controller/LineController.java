package wooteco.subway.admin.controller;

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
import wooteco.subway.admin.dto.EdgeCreateRequest;
import wooteco.subway.admin.dto.EdgeDeleteRequest;
import wooteco.subway.admin.dto.EdgeResponse;
import wooteco.subway.admin.dto.LineCreateRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineUpdateRequest;
import wooteco.subway.admin.service.LineService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("lines")
public class LineController {

    private final LineService lineService;

    private boolean isFirst = true;

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
    public ResponseEntity<Long> updateLine(@PathVariable("id") @Valid @NotNull(message = "노선 고유 값이 없습니다.") Long lineId,
                                           @RequestBody LineUpdateRequest lineUpdateRequest) {
        lineService.updateLine(lineId, lineUpdateRequest.toLine());
        return ResponseEntity.ok(lineId);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Long> deleteLine(@PathVariable("id") Long lineId) {
        lineService.deleteLineById(lineId);
        return ResponseEntity.ok(lineId);
    }

    @GetMapping("{id}/edge")
    public ResponseEntity<List<EdgeResponse>> getEdgesByLineId(@PathVariable(name = "id") final Long lineId) {
        return ResponseEntity.ok(lineService.findEdgeResponseByLineId(lineId));
    }

    @PostMapping("{id}/edge")
    public ResponseEntity<Long> createEdge(@PathVariable(name = "id") final Long lineId, @RequestBody final EdgeCreateRequest edgeCreateRequest) {
        lineService.addEdge(lineId, edgeCreateRequest);
        return new ResponseEntity<>(1L, HttpStatus.CREATED);
    }

    @DeleteMapping("{id}/edge")
    public ResponseEntity<Void> deleteEdge(@PathVariable(name = "id") final Long lineId, @RequestBody @Valid final EdgeDeleteRequest edgeDeleteRequest) {
        lineService.removeEdge(lineId, edgeDeleteRequest);
        return ResponseEntity.noContent().build();
    }
}
