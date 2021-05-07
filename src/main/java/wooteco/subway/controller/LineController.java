package wooteco.subway.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.controller.dto.request.line.LineCreateRequestDto;
import wooteco.subway.controller.dto.request.line.LineUpdateRequestDto;
import wooteco.subway.controller.dto.response.line.LineResponseDto;
import wooteco.subway.service.LineService;

@RequestMapping("/lines")
@RestController
public class LineController {
    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping(value = "",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponseDto> createLine(@RequestBody LineCreateRequestDto lineCreateRequestDto) {
        LineResponseDto lineResponseDto = lineService.createLine(lineCreateRequestDto);
        return ResponseEntity
            .created(URI.create("/lines/" + lineResponseDto.getId()))
            .body(lineResponseDto);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponseDto> showOneLine(@PathVariable Long id) {
        LineResponseDto lineResponseDto = lineService.getLineById(id);
        return ResponseEntity.ok()
            .body(lineResponseDto);
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponseDto>> showAllLines() {
        List<LineResponseDto> allLineResponses = lineService.getAllLines();
        return ResponseEntity.ok()
            .body(allLineResponses);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineUpdateRequestDto lineUpdateRequestDto) {
        lineService.updateLine(id, lineUpdateRequestDto);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLinById(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }
}
