package wooteco.subway.line.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.service.LineService;

import java.net.URI;
import java.util.List;

@RestController
public class LineController {
    private static final Logger log = LoggerFactory.getLogger("console");

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse newLine = lineService.save(lineRequest);
        log.info("An INFO Message : {}", newLine.getName() + " 노선 생성 성공");
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(newLine);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> allLines = lineService.findAllLines();
        log.info("An INFO Message : {}", "지하철 모든 노선 조회 성공");
        return ResponseEntity.ok().body(allLines);
    }

    @GetMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        LineResponse line = lineService.findLine(id);
        log.info("An INFO Message : {}", line.getName() + "노선 조회 성공");
        return ResponseEntity.ok().body(line);
    }

    @PutMapping(value = "/lines/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.updateLine(id, lineRequest);
        log.info("An INFO Message : {}", "노선 정보 수정 완료");
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteLine(id);
        log.info("An INFO Message : {}", "노선 삭제 성공");
        return ResponseEntity.noContent().build();
    }
}
