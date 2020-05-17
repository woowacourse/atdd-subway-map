package wooteco.subway.admin.line.controller;

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
import wooteco.subway.admin.common.response.DefaultResponse;
import wooteco.subway.admin.line.service.LineService;
import wooteco.subway.admin.line.service.dto.edge.EdgeCreateRequest;
import wooteco.subway.admin.line.service.dto.edge.EdgeDeleteRequest;
import wooteco.subway.admin.line.service.dto.edge.EdgeResponse;
import wooteco.subway.admin.line.service.dto.line.LineCreateRequest;
import wooteco.subway.admin.line.service.dto.line.LineEdgeResponse;
import wooteco.subway.admin.line.service.dto.line.LineResponse;
import wooteco.subway.admin.line.service.dto.line.LineUpdateRequest;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<DefaultResponse<Void>> createLine(@RequestBody @Valid LineCreateRequest lineCreateRequest) {
        Long id = lineService.save(lineCreateRequest);
        return ResponseEntity.created(URI.create("/lines/" + id)).body(DefaultResponse.empty());
    }

    @GetMapping
    public ResponseEntity<DefaultResponse<List<LineResponse>>> getLines() {
        return ResponseEntity.ok(DefaultResponse.of(lineService.getLineResponses()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DefaultResponse<LineResponse>> findByLineId(@PathVariable("id") Long lineId) {
        return ResponseEntity.ok(DefaultResponse.of(lineService.findLineWithStationsById(lineId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DefaultResponse<Void>> updateLine(@PathVariable("id") @Valid @NotNull(message = "노선 고유 값이 없습니다.") Long lineId,
                                                            @RequestBody LineUpdateRequest lineUpdateRequest) {
        lineService.updateLine(lineId, lineUpdateRequest.toLine());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DefaultResponse<Void>> deleteLine(@PathVariable("id") Long lineId) {
        lineService.deleteLineById(lineId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/edges")
    public ResponseEntity<DefaultResponse<List<EdgeResponse>>> findEdgesByLineId(@PathVariable(name = "id") final Long lineId) {
        return ResponseEntity.ok(DefaultResponse.of(lineService.findEdgesByLineId(lineId)));
    }

    @PostMapping("/{id}/edges")
    public ResponseEntity<DefaultResponse<Void>> createEdge(@PathVariable(name = "id") final Long lineId, @RequestBody @Valid final EdgeCreateRequest edgeCreateRequest) {
        lineService.addEdge(lineId, edgeCreateRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/edges")
    public ResponseEntity<DefaultResponse<Void>> deleteEdge(@PathVariable(name = "id") final Long lineId, @RequestBody @Valid final EdgeDeleteRequest edgeDeleteRequest) {
        lineService.removeEdge(lineId, edgeDeleteRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/edges")
    public ResponseEntity<DefaultResponse<List<LineEdgeResponse>>> getAllLineAndEdges() {
        return ResponseEntity.ok(DefaultResponse.of(lineService.getAllLineEdge()));
    }
}
