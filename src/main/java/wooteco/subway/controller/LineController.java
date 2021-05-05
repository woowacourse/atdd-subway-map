package wooteco.subway.controller;

import com.google.common.io.LineReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sun.security.provider.certpath.ResponderId;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.station.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.service.LineService;

@RestController
public class LineController {

    private final LineService lineService = new LineService();

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line newLine = lineService.createLine(lineRequest.getName(), lineRequest.getColor());
        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(),
            newLine.getColor(), new ArrayList<>());
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping(value = "/lines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> shwLines() {
        List<Line> lines = lineService.findAll();
        List<LineResponse> lineResponses = lines.stream()
            .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor(),
                new ArrayList<>()))
            .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line line = lineService.findById(id);
        LineResponse lineResponse = new LineResponse(line.getId(), line.getName(), line.getColor(), new ArrayList<>());
        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<Void> editLine(@PathVariable long id, @RequestBody LineRequest lineRequest) {
        lineService.editLine(id, lineRequest.getName(), lineRequest.getColor());
        return ResponseEntity.ok().build();
    }
}
