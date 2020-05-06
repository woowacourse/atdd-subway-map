package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.Optional;

import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.repository.LineRepository;

@RestController
public class LineController {

    private final LineRepository lineRepository;

    public LineController(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @PostMapping("/lines")
    public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
        Line line = lineRequest.toLine();
        Line persistLine = lineRepository.save(line);

        return ResponseEntity.created(URI.create("/lines/" + persistLine.getId()))
            .body(LineResponse.of(persistLine));
    }

    @GetMapping("/lines")
    public ResponseEntity getLines() {
        return ResponseEntity.ok().body(LineResponse.listOf(lineRepository.findAll()));
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity getLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(LineResponse.of(lineRepository.findById(id).get()));
    }


    @PutMapping("/lines/{id}")
    public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        Line line = lineRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당하는 노선이 없습니다!"));
        line.update(lineRequest.toLine());
        Line persistLine = lineRepository.save(line);
        return ResponseEntity.ok().body(LineResponse.of(persistLine));
    }

    @DeleteMapping("/lines/{id}")
    public void deleteLine(@PathVariable Long id) {
        lineRepository.deleteById(id);
    }
}
