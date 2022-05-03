package wooteco.subway.ui;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineDto;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.service.LineService;

@Controller
public class LineController {

    private final LineDao lineDao = new LineDao();
    private final LineService lineService = new LineService(lineDao);

    @PostMapping("/lines")
    public ResponseEntity<LineDto> createLine(@RequestBody LineRequest lineRequest) {
        try {
            Line newLine = lineService.save(lineRequest);
            LineDto lineResponse = new LineDto(newLine.getId(), newLine.getName(), newLine.getColor());
            return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = "/lines"
            , produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<LineDto>> showLines() {
        List<Line> lines = lineService.findAll();
        List<LineDto> lineResponses = lines.stream()
                .map(it -> new LineDto(it.getId(), it.getName(), it.getColor()))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @DeleteMapping("/lines")
    public ResponseEntity<Void> deleteLines() {
        lineService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
