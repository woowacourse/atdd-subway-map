package wooteco.subway.admin.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.service.LineService;

@Controller
public class LineController {

    @Autowired
    private LineService lineService;

    @PostMapping("/lines")
    @ResponseBody
    public ResponseEntity createLine(@RequestBody LineRequest lineRequest) {
        Line line = new Line(
            lineRequest.getName(),
            lineRequest.getStartTime(),
            lineRequest.getEndTime(),
            lineRequest.getIntervalTime()
        );

        return ResponseEntity.created(URI.create("")).body(lineService.save(line));
    }

    @GetMapping("/lines")
    @ResponseBody
    public List<LineResponse> getLines() {
        List<Line> lines = lineService.showLines();

        return lineService.showLines().stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    @GetMapping("/lines/{id}")
    @ResponseBody
    public LineResponse getLine(@PathVariable Long id) {
        return lineService.findLineWithStationsById(id);
    }
}
