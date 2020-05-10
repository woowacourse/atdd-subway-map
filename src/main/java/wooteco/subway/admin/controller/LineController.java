package wooteco.subway.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.service.LineService;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalTime;
import java.util.List;

@RestController
public class LineController {

    @Autowired
    private LineService lineService;

    @GetMapping("/admin-line")
    public ModelAndView adminLine() {
        ModelAndView mv = new ModelAndView("admin-line");
        mv.addObject("lines", lineService.findAllLines());
        return mv;
    }

    @PostMapping("/lines")
    public ResponseEntity<?> create(
            @RequestBody LineRequest request
    ) throws URISyntaxException {
        String name = request.getName();
        String color = request.getColor();
        LocalTime startTime = request.getStartTime();
        LocalTime endTime = request.getEndTime();
        int intervalTime = request.getIntervalTime();

        Line line = new Line(name, color, startTime, endTime, intervalTime);
        Line created = lineService.save(line);

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
            @PathVariable("id") Long id, @RequestBody LineRequest request
    ) {
        String name = request.getName();
        String color = request.getColor();
        LocalTime startTime = request.getStartTime();
        LocalTime endTime = request.getEndTime();
        int intervalTime = request.getIntervalTime();

        Line line = lineService.findLineById(id);
        line.update(new Line(name, color, startTime, endTime, intervalTime));

        Line updated = lineService.save(line);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<?> delete(
            @PathVariable("id") Long id
    ) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }
}
