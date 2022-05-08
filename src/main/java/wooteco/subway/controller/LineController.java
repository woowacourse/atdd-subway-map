package wooteco.subway.controller;

import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.LineSaveRequest;
import wooteco.subway.dto.LineUpdateRequest;
import wooteco.subway.service.LineService;

@RestController
@RequestMapping("/lines")
@Validated
public class LineController {

    private final LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody @Valid LineSaveRequest lineSaveRequest) {
        LineResponse response = lineService.save(lineSaveRequest);
        return ResponseEntity.created(URI.create("/lines/" + response.getId())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        return ResponseEntity.ok().body(lineService.findAll());
    }

    @GetMapping("/{lineId}")
    public ResponseEntity<LineResponse> showLine(
            @PathVariable @Positive(message = "노선의 id는 양수 값만 들어올 수 있습니다.") Long lineId) {
        LineResponse response = lineService.findById(lineId);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{lineId}")
    public ResponseEntity<Void> updateLine(@PathVariable @Positive(message = "노선의 id는 양수 값만 들어올 수 있습니다.") Long lineId,
                                           @RequestBody @Valid LineUpdateRequest lineUpdateRequest) {
        lineService.update(lineId, lineUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineId}")
    public ResponseEntity<Void> deleteLine(@PathVariable @Positive(message = "노선의 id는 양수 값만 들어올 수 있습니다.") Long lineId) {
        lineService.delete(lineId);
        return ResponseEntity.noContent().build();
    }
}
