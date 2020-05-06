package wooteco.subway.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.repository.LineRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class LineController {

    @Autowired
    private LineRepository lineRepository;

    @PostMapping("/lines")
    public ResponseEntity<?> create(
            @RequestBody LineRequest request
    ) throws URISyntaxException {
        String name = request.getName();
        LocalTime startTime = request.getStartTime();
        LocalTime endTime = request.getEndTime();
        int intervalTime = request.getIntervalTime();

        Line line = new Line(name, startTime, endTime, intervalTime);
        Line created = lineRepository.save(line);

        final URI url = new URI("/lines/" + created.getId());
        return ResponseEntity.created(url).body("{}");
    }

    @GetMapping("/lines")
    public List<LineResponse> lines() {
        return LineResponse.listOf(lineRepository.findAll());
    }

    @GetMapping("/lines/{id}")
    public LineResponse line(
            @PathVariable("id") Long id
    ) {
        return LineResponse.of(lineRepository.findById(id)
                .orElseThrow(NoSuchElementException::new));
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<?> update(
            @PathVariable("id") Long id, @RequestBody LineRequest request
    ) {
        String name = request.getName();
        LocalTime startTime = request.getStartTime();
        LocalTime endTime = request.getEndTime();
        int intervalTime = request.getIntervalTime();

        Line line = lineRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
        line.update(new Line(name, startTime, endTime, intervalTime));

        Line updated = lineRepository.save(line);
        return ResponseEntity.ok(updated);
    }
}
