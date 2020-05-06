package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.repository.LineRepository;

@RequestMapping("/lines")
@RestController
public class LineController {
    private LineRepository lineRepository;

    public LineController(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @PostMapping
    ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = lineRequest.toLine();
        Line persistLine = lineRepository.save(line);

        return ResponseEntity
                .created(URI.create("/lines/" + persistLine.getId()))
                .build();
    }

    @GetMapping
    ResponseEntity<List<LineResponse>> getLines() {
        List<Line> lines = lineRepository.findAll();

        return ResponseEntity
                .ok(LineResponse.listOf(lines));
    }

    @GetMapping("/{id}")
    ResponseEntity<LineResponse> getLine(@PathVariable Long id) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id입니다."));

        return ResponseEntity
                .ok(LineResponse.of(line));
    }

    @PutMapping("/{id}")
    ResponseEntity<LineResponse> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        Line line = lineRequest.toLine();
        Line persistLine = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 id입니다."));
        persistLine.update(line);
        lineRepository.save(persistLine);

        return ResponseEntity
                .ok()
                .build();
    }
}
