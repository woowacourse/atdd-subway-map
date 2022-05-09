package wooteco.subway.ui;

import java.net.URI;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.service.LineService;

@RestController
public class LineController {

    private final LineService service;

    public LineController(LineService service) {
        this.service = service;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse response = service.save(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + response.getId())).body(response);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(service.findAll());
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.findOne(id));
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<Void> modifyLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        service.update(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
