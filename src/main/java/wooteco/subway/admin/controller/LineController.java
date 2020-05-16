package wooteco.subway.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.admin.dto.LineRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineWithStationsResponse;
import wooteco.subway.admin.dto.domain.LineDto;
import wooteco.subway.admin.service.LineService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineWithStationsResponse> createLine(@RequestBody @Valid LineRequest view) {
        LineDto lineDto = view.toLineDto();
        LineDto persistLine = lineService.save(lineDto);

        return ResponseEntity
                .created(URI.create("/lines/" + persistLine.getId()))
                .body(LineWithStationsResponse.of(persistLine));
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineWithStationsResponse>> showLines() {
        List<LineWithStationsResponse> lineWithStationsResponses = lineService.showLines();

        return ResponseEntity
                .ok()
                .body(lineWithStationsResponses);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineWithStationsResponse> findLineWithStationsBy(@PathVariable(name = "id") Long id) {
        LineWithStationsResponse lineWithStationsResponse = lineService.findLineWithStationsBy(id);

        return ResponseEntity
                .ok()
                .body(lineWithStationsResponse);
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<LineResponse> updateLineBy(@PathVariable(name = "id") Long id, @RequestBody LineRequest view) {
        LineDto persistLine = lineService.updateLine(id, view);
        return ResponseEntity
                .ok()
                .body(LineResponse.of(persistLine));
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> deleteLineBy(@PathVariable(name = "id") Long id) {
        lineService.deleteLineBy(id);

        return ResponseEntity
                .ok()
                .build();
    }
}
