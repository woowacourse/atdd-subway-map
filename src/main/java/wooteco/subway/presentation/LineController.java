package wooteco.subway.presentation;

import java.net.URI;
import java.util.List;
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
import wooteco.subway.dto.LineResponseV2;

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

    @GetMapping("/{id}")
    public ResponseEntity<LineResponseV2> showSingleLine(@PathVariable Long id) {
        return ResponseEntity.ok().body(lineService.findLine(id));
    }

    @GetMapping
    public ResponseEntity<List<LineResponseV2>> showLines() {
        return ResponseEntity.ok().body(lineService.findLines());
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
