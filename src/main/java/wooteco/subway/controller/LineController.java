package wooteco.subway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.service.LineService;
import wooteco.subway.dao.entity.LineEntity;
import wooteco.subway.dao.line.LineDao;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.LineUpdateRequest;
import wooteco.subway.ErrorResponse;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineDao lineDao;
    private final LineService lineService;
    private final Logger logger = LoggerFactory.getLogger(LineController.class);

    public LineController(LineDao lineDao, LineService lineService) {
        this.lineDao = lineDao;
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> create(@RequestBody LineRequest lineRequest) {
        LineResponse response = lineService.save(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + response.getId())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> getLines() {
        List<LineEntity> lineEntities = lineDao.findAll();
        // TODO : 구간에 포함된 지하철 역 조회 로직
        List<LineResponse> lineResponses = lineEntities.stream()
                .map(it -> new LineResponse(it.id(), it.name(), it.color(), Collections.emptyList()))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> getLine(@PathVariable Long id) {
        LineEntity findLineEntity = lineDao.findById(id).orElseThrow(() -> new IllegalArgumentException("없는 노선임!"));
        LineResponse response = new LineResponse(findLineEntity.id(), findLineEntity.name(), findLineEntity.color(), Collections.emptyList());
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineUpdateRequest lineUpdateRequest) {
        lineDao.update(id, lineUpdateRequest.getName(), lineUpdateRequest.getColor());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineDao.delete(id);
        return ResponseEntity.noContent().build();
    }
}
