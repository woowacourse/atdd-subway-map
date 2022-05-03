package wooteco.subway.ui;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
}
