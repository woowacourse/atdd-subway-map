package wooteco.subway.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.controller.dto.LineRequest;
import wooteco.subway.controller.dto.LineResponse;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.DuplicateNameException;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/lines")
@RestController
public class LineController {

    private final LineDao lineDao;

    public LineController(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        boolean existsName = lineDao.findByName(lineRequest.getName()).isPresent();
        if (existsName) {
            throw new DuplicateNameException("이미 존재하는 노선 이름입니다.");
        }

        final Line newLine = lineDao.save(lineRequest);
        final LineResponse lineResponse = new LineResponse(newLine);
        final URI uri = URI.create("/lines/" + newLine.getId());
        return ResponseEntity.created(uri).body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        final List<LineResponse> lineResponses = lineDao.findAll()
                                                        .stream()
                                                        .map(LineResponse::new)
                                                        .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        final Line line = lineDao.findById(id)
                                 .orElseThrow(() -> new IllegalArgumentException("해당 ID에 해당하는 노선이 존재하지 않습니다."));
        LineResponse lineResponse = new LineResponse(line);
        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> editLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        boolean existsName = lineDao.findByName(lineRequest.getName()).isPresent();
        if (existsName) {
            throw new DuplicateNameException("이미 존재하는 노선 이름입니다.");
        }

        lineDao.update(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineDao.delete(id);
        return ResponseEntity.noContent().build();
    }
}
