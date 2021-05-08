package wooteco.subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.line.exception.DuplicateLineNameException;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineRepository lineRepository;

    @Autowired
    public LineController(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLines(@RequestBody LineRequest lineRequest) {
        String name = lineRequest.getName();
        String color = lineRequest.getColor();
        validateDuplicateLineName(name);

        Line save = this.lineRepository.save(new Line(name, color));
        LineResponse lineResponse = new LineResponse(save.getId(), save.getName(), save.getColor());

        URI location = URI.create("/lines/" + lineResponse.getId());

        return ResponseEntity
                .created(location)
                .body(lineResponse);
    }

    private void validateDuplicateLineName(String name) {
        this.lineRepository.findByName(name).ifPresent(line -> {
            throw new DuplicateLineNameException(name);
        });
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResponses = lineRepository.findAll().stream()
                .map(it -> new LineResponse(it.getId(), it.getName(), it.getColor()))
                .collect(Collectors.toList());

        return ResponseEntity
                .ok()
                .body(lineResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> getLine(@PathVariable long id) {
        return ResponseEntity
                .ok()
                .body(LineResponse.from(lineRepository.findById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LineResponse> updateLine(@PathVariable long id, @RequestBody LineRequest lineRequest) {
        String name = lineRequest.getName();
        String color = lineRequest.getColor();
        lineRepository.update(id, new Line(name, color));

        return ResponseEntity
                .noContent()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<LineResponse> deleteLine(@PathVariable long id) {
        lineRepository.delete(id);
        return ResponseEntity
                .noContent()
                .build();
    }

}
