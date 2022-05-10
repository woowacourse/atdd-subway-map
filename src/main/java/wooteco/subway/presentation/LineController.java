package wooteco.subway.presentation;

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
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.application.LineService;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineRequestV2;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.LineResponseV2;
import wooteco.subway.exception.NoSuchLineException;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineDao lineDao;
    private final LineService lineService;

    public LineController(final LineDao lineDao, final LineService lineService) {
        this.lineDao = lineDao;
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponseV2> createLine(@RequestBody LineRequestV2 lineRequest) {
        LineResponseV2 response = lineService.createLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + response.getId())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineDao.findAll();
        List<LineResponse> lineResponses = lines.stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        LineResponse lineResponse = LineResponse.from(lineDao.findById(id)
                .orElseThrow(NoSuchLineException::new));
        return ResponseEntity.ok().body(lineResponse);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        lineDao.update(new Line(id, lineRequest.getName(), lineRequest.getColor()));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        lineDao.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
