package wooteco.subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.dao.line.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.line.LineInfo;
import wooteco.subway.dto.line.LineRequest;
import wooteco.subway.dto.line.LineResponse;
import wooteco.subway.service.LineService;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineDao lineDao;
    private final LineService lineService;

    public LineController(LineDao lineDao, LineService lineService) {
        this.lineDao = lineDao;
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> create(@Validated(LineInfo.save.class) @RequestBody LineRequest lineRequest) {
        LineResponse response = lineService.save(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + response.getId())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> getLines() {
        List<Line> line = lineDao.findAll();
        // TODO : 구간에 포함된 지하철 역 조회 로직
        List<LineResponse> lineResponses = line.stream()
                .map(it -> new LineResponse(it.id(), it.name(), it.color(), Collections.emptyList()))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("/{id:[\\d]+}")
    public ResponseEntity<LineResponse> getLine(@PathVariable Long id) {
        Line findLine = lineDao.findById(id).orElseThrow(() -> new IllegalArgumentException("없는 노선임!"));
        LineResponse response = new LineResponse(findLine.id(), findLine.name(), findLine.color(), Collections.emptyList());
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{id:[\\d]+}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id,
                                           @Valid @RequestBody LineRequest lineRequest) {
        lineDao.update(id, lineRequest.getName(), lineRequest.getColor());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id:[\\d]+}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineDao.delete(id);
        return ResponseEntity.noContent().build();
    }
}
