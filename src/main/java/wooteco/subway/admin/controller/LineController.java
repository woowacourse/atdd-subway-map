package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.repository.LineRepository;

import javax.xml.ws.Response;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
public class LineController {
    private final LineRepository lineRepository;

    public LineController(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest view) {
        Line line = view.toLine();
        Line persistLine = lineRepository.save(line);

        return ResponseEntity
                .created(URI.create("/lines/" + persistLine.getId()))
                .body(LineResponse.of(persistLine));
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> getLine(@PathVariable Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("노선을 찾을수 없습니다."));

        return ResponseEntity.ok()
                .body(LineResponse.of(line));
    }

    @PutMapping("/lines/{id}")
    public void updateLine(@PathVariable Long id, @RequestBody LineRequest view) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("노선을 찾을수 없습니다."));

        line.update(view.toLine());
        lineRepository.save(line);
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> getLines(){
        List<Line> lines = lineRepository.findAll();

        return ResponseEntity.ok()
                .body(LineResponse.listOf(lines));
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<LineResponse> deleteLine(@PathVariable Long id) {
        lineRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}