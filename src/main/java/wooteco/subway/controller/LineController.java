package wooteco.subway.controller;


import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.request.LineRequestDto;
import wooteco.subway.dto.response.LineResponseDto;
import wooteco.subway.repository.dao.JdbcLineDao;
import wooteco.subway.service.LineService;

@RestController
public class LineController {

    private final LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponseDto> createLine(@RequestBody final LineRequestDto lineRequestDto) {
        final Line newLine = lineService.register(lineRequestDto.getName(), lineRequestDto.getColor());
        final LineResponseDto lineResponseDto =
                new LineResponseDto(newLine.getId(), newLine.getName(), newLine.getColor(), new ArrayList<>());
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponseDto);
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponseDto>> showLines() {
        final List<Line> lines = lineService.searchAll();
        final List<LineResponseDto> lineResponseDtos = lines.stream()
                .map(line -> new LineResponseDto(line.getId(), line.getName(), line.getColor(), new ArrayList<>()))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponseDtos);
    }

    @GetMapping("/lines/{id}")
    public ResponseEntity<LineResponseDto> showLine(@PathVariable final Long id) {
        final Line line = lineService.searchById(id);
        return ResponseEntity.ok()
                .body(new LineResponseDto(line.getId(), line.getName(), line.getColor(), new ArrayList<>()));
    }

    @PutMapping("/lines/{id}")
    public ResponseEntity<Void> modifyLine(@PathVariable final Long id,
                                           @RequestBody final LineRequestDto lineRequestDto) {
        lineService.modify(id, lineRequestDto.getName(), lineRequestDto.getColor());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> removeLine(@PathVariable final Long id) {
        lineService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
