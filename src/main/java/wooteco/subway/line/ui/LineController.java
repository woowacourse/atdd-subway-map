package wooteco.subway.line.ui;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.repository.LineDao;
import wooteco.subway.line.repository.LineRepositoryImpl;
import wooteco.subway.line.ui.dto.LineRequest;
import wooteco.subway.line.ui.dto.LineResponse;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("lines")
public class LineController {

    private final LineService lineService;

    public LineController() {
        this.lineService =  new LineService(new LineRepositoryImpl(new LineDao()));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> createNewLine(@RequestBody LineRequest lineRequest) throws URISyntaxException {
        final Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        final Line savedLine = lineService.save(line);

        return ResponseEntity
                .created(
                        new URI("/lines/" + savedLine.getId())
                )
                .body(
                        new LineResponse(
                                savedLine.getId(),
                                savedLine.getName(),
                                savedLine.getColor()
                        )
                );
    }

}
