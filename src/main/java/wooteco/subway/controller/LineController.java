package wooteco.subway.controller;


import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.request.LineRequestDto;
import wooteco.subway.dto.response.LineResponseDto;
import wooteco.subway.repository.dao.LineDao;
import wooteco.subway.service.LineService;

@RestController
public class LineController {

    private final LineService lineService = new LineService(new LineDao());

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
}
