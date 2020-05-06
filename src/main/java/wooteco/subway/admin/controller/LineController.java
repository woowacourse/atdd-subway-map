package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import wooteco.subway.admin.service.LineService;

@RestController
@RequestMapping("/lines")
public class LineController {

    @Autowired
    private LineService lineService;

    @PostMapping
    public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
        System.out.println(lineRequest.toString());
        Line line = new Line(
            lineRequest.getName(),
            lineRequest.getStartTime(),
            lineRequest.getEndTime(),
            lineRequest.getIntervalTime()
        );
        System.out.println(line);
        return ResponseEntity.created(URI.create("")).body(lineService.save(line));
    }

    @GetMapping
    public List<LineResponse> getLines() {
        List<Line> lines = lineService.showLines();

        return lineService.showLines().stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity getLine(@PathVariable Long id) {
        return ResponseEntity.ok(lineService.findLineWithStationsById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        Line line = new Line(
            lineRequest.getName(),
            lineRequest.getStartTime(),
            lineRequest.getEndTime(),
            lineRequest.getIntervalTime()
        );
        lineService.updateLine(id, line);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.ok().build();
    }

}
