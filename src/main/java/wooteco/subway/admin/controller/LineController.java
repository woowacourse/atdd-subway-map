package wooteco.subway.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.service.LineService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
public class LineController {

    @Autowired
    private LineService lineService;

    @PostMapping("/lines")
    public ResponseEntity<?> create(
            @RequestBody LineRequest lineRequest
    ) throws URISyntaxException {

        Line created = lineService.save(lineRequest.toLine());

        final URI url = new URI("/lines/" + created.getId());
        return ResponseEntity.created(url).body("{}");
    }

    @GetMapping("/lines")
    public List<LineResponse> lines() {
        return lineService.findAllLines();
    }

    @GetMapping("/lines/{id}")
    public LineResponse line(
            @PathVariable("id") Long id
    ) {
        return LineResponse.of(lineService.findLineById(id));
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<?> update(
            @PathVariable("id") Long id,
            @RequestBody LineRequest lineRequest
    ) {
        Line line = lineRequest.toLine();
        lineService.updateLine(id, line);
        return ResponseEntity.ok(line);
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<?> delete(
            @PathVariable("id") Long id
    ) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }
}
