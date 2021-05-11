package wooteco.subway.line;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.section.SectionDao;
import wooteco.subway.section.SectionDto;

@RestController
@RequestMapping("/lines")
public class LineController {

    private LineDao lineDao;
    private SectionDao sectionDao;

    @Autowired
    public LineController(LineDao lineDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    @PostMapping("")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {

        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        long id = lineDao.save(line);
        SectionDto sectionDto = new SectionDto(id, lineRequest.getUpStationId(),
            lineRequest.getDownStationId(),
            lineRequest.getDistance());
        sectionDao.save(sectionDto);
        LineResponse lineResponse = new LineResponse(id, line.getName(),
            line.getColor());
        return ResponseEntity.created(URI.create("/lines/" + id)).body(lineResponse);
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLine() {
        List<Line> lines = lineDao.findAll();
        List<LineResponse> lineResponses = lines.stream()
            .map(it -> new LineResponse(it.getId(), it.getName(), it.getColor()))
            .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLineDetail(@PathVariable Long id) {
        Line line = lineDao.find(id);
        return ResponseEntity.ok()
            .body(new LineResponse(line.getId(), line.getName(), line.getColor()));
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> modifyLineDetail(@PathVariable Long id,
        @RequestBody LineRequest lineRequest) {
        lineDao.modify(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineDao.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity handleNameDuplication() {
        return ResponseEntity.status(409).build();
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity handleNoSuchLine() {
        return ResponseEntity.badRequest().build();
    }
}
