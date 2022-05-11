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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.request.LineRequestDto;
import wooteco.subway.dto.request.SectionRequestDto;
import wooteco.subway.dto.response.LineResponseDto;
import wooteco.subway.service.LineService;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponseDto> createLine(@RequestBody final LineRequestDto lineRequestDto) {
        final Line line = lineService.registerLine(lineRequestDto);
        final LineResponseDto lineResponseDto = new LineResponseDto(line);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(lineResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<LineResponseDto>> showLines() {
        final List<Line> lines = lineService.searchAllLines();
        final List<LineResponseDto> lineResponseDtos = lines.stream()
                .map(line -> new LineResponseDto(line))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponseDtos);
    }

    @GetMapping("/{lineId}")
    public ResponseEntity<LineResponseDto> showLine(@PathVariable final Long lineId) {
        final Line line = lineService.searchLineById(lineId);
        final LineResponseDto lineResponseDto = new LineResponseDto(line);
        return ResponseEntity.ok().body(lineResponseDto);
    }

    @PutMapping("/{lineId}")
    public ResponseEntity<Void> modifyLine(@PathVariable final Long lineId,
                                           @RequestBody final LineRequestDto lineRequestDto) {
        lineService.modifyLine(lineId, lineRequestDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineId}")
    public ResponseEntity<Void> removeLine(@PathVariable final Long lineId) {
        lineService.removeLine(lineId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity<Void> createSection(@PathVariable final Long lineId,
                                              @RequestBody final SectionRequestDto sectionRequestDto) {
        lineService.registerSection(lineId, sectionRequestDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity<Void> removeSection(@PathVariable final Long lineId, @RequestParam Long stationId) {
        lineService.removeSection(lineId, stationId);
        return ResponseEntity.ok().build();
    }
}
