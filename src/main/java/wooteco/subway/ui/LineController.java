package wooteco.subway.ui;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.service.LineService;
import wooteco.subway.service.SectionService;

@Controller
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;
    private final SectionService sectionService;

    public LineController(LineService lineService, SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line newLine = lineService.save(lineRequest.toLine());
        sectionService.save(lineRequest.toSection(newLine.getId()));
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId()))
                .body(LineResponse.of(newLine, sectionService.findStationsOfLine(newLine.getId())));
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> lineResponses = lineService.findAll()
                .stream()
                .map(line -> LineResponse.of(line, sectionService.findStationsOfLine(line.getId())))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> putLines(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineService.update(id, lineRequest.toLine());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
