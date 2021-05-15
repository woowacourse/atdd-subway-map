package wooteco.subway.line.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import wooteco.subway.exception.SubwayException;
import wooteco.subway.line.dto.request.LineCreateRequest;
import wooteco.subway.line.dto.request.LineUpdateRequest;
import wooteco.subway.line.dto.response.LineCreateResponse;
import wooteco.subway.line.dto.response.LineResponse;
import wooteco.subway.line.dto.response.LineStationsResponse;
import wooteco.subway.line.service.LineService;
import wooteco.subway.section.dto.request.SectionCreateRequest;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineCreateResponse> createLine(@RequestBody @Valid LineCreateRequest request, Errors errors) {
        if (errors.hasErrors()) {
            throw new SubwayException("올바른 값이 아닙니다.");
        }
        LineCreateResponse response = lineService.create(request);
        return ResponseEntity.created(URI.create("/lines/" + response.getId()))
                .body(response);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<LineResponse> allLines = lineService.findAll();
        return ResponseEntity.ok().body(allLines);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineStationsResponse> showLine(@PathVariable Long id) {
        LineStationsResponse response = lineService.findBy(id);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(value = "/{id}/sections", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addSection(@PathVariable Long id,
                                           @RequestBody @Valid SectionCreateRequest request, Errors errors) {
        if (errors.hasErrors()) {
            throw new SubwayException("올바른 값이 아닙니다.");
        }
        lineService.addSection(id, request);
        return ResponseEntity.ok().build();
    }


    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateLine(@PathVariable Long id,
                                           @RequestBody @Valid LineUpdateRequest request, Errors errors) {
        if (errors.hasErrors()) {
            throw new SubwayException("올바른 값이 아닙니다.");
        }

        lineService.update(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable Long id, @RequestParam Long stationId) {
        lineService.deleteStationInSection(id, stationId);
        return ResponseEntity.noContent().build();
    }
}
