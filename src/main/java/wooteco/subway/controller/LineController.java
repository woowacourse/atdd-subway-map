package wooteco.subway.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wooteco.subway.dto.info.LineInfoToUpdate;
import wooteco.subway.dto.info.RequestLineInfo;
import wooteco.subway.dto.info.ResponseLineInfo;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.dto.response.LineResponse;
import wooteco.subway.service.LineService;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping()
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        RequestLineInfo lineInfo = LineConverter.toInfo2(lineRequest);
        LineResponse lineResponse = LineConverter.toResponse(lineService.save(lineInfo));
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId())).body(lineResponse);
    }

    @GetMapping()
    public ResponseEntity<List<LineResponse>> showLines() {
        List<ResponseLineInfo> lineInfos = lineService.findAll();
        List<LineResponse> lineResponses = lineInfos.stream()
            .map(info -> LineConverter.toResponse(info))
            .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        LineResponse lineResponse = LineConverter.toResponse(lineService.find(id));
        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        LineInfoToUpdate lineInfoToUpdate = LineConverter.toInfo(id, lineRequest);
        lineService.update(lineInfoToUpdate);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
