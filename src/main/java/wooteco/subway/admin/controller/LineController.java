package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.service.LineService;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    @Autowired
    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity createLine(@RequestBody LineRequest request) {
        Line line = request.toLine();
        Line persistLine = lineService.save(line);

        return ResponseEntity
            .created(URI.create("/lines/" + persistLine.getId()))
            .body(LineResponse.of(persistLine));
    }

    @GetMapping
    public ResponseEntity getLines() {
        List<LineResponse> lines = lineService.showLines();
        return ResponseEntity
            .ok()
            .location(URI.create("/lines"))
            .body(lines);
    }

    @GetMapping("/{id}")
    public ResponseEntity getLine(@PathVariable Long id) {
        return ResponseEntity
            .ok()
            .location(URI.create("/lines/" + id))
            .body(lineService.findLineWithStationsById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity updateLine(@PathVariable Long id, @RequestBody LineRequest request) {
        lineService.updateLine(id, request.toLine());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/find")
    public ResponseEntity findByName(@RequestBody StationCreateRequest request) {
        LineResponse response = lineService.findByName(request.getName());
        return ResponseEntity.ok(response);
    }
}
