package wooteco.subway.line.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.line.application.SectionService;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.LineDao;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.dto.LineSaveRequestDto;
import wooteco.subway.line.dto.LineUpdateRequest;
import wooteco.subway.common.ResponseError;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineDao lineDao;
    private final SectionService sectionService;
    private final Logger logger = LoggerFactory.getLogger(LineController.class);

    public LineController(LineDao lineDao, SectionService sectionService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> create(@RequestBody LineSaveRequestDto requestDto) {
        Line newLine = lineDao.save(new Line(requestDto.getName(), requestDto.getColor()));

         new SectionService();
        // TODO : 구간에 포함된 지하철 역 조회 로직
        LineResponse response = new LineResponse(newLine.id(), newLine.name(), newLine.color(), Collections.emptyList());
        return ResponseEntity.created(URI.create("/lines/" + newLine.id())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> getLines() {
        List<Line> lines = lineDao.findAll();
        // TODO : 구간에 포함된 지하철 역 조회 로직
        List<LineResponse> lineResponses = lines.stream()
                .map(it -> new LineResponse(it.id(), it.name(), it.color(), Collections.emptyList()))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> getLine(@PathVariable Long id) {
        Line findLine = lineDao.findById(id).orElseThrow(() -> new IllegalArgumentException("없는 노선임!"));
        LineResponse response = new LineResponse(findLine.id(), findLine.name(), findLine.color(), Collections.emptyList());
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

    @ExceptionHandler(value = {IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ResponseError> handleException(RuntimeException e) {
        logger.info(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseError(e.getMessage()));
    }


}
