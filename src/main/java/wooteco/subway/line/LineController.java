package wooteco.subway.line;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.exception.DeleteMinimumSizeException;
import wooteco.subway.exception.ShortDistanceException;
import wooteco.subway.section.Section;
import wooteco.subway.section.SectionService;

@RestController
@RequestMapping("/lines")
public class LineController {

    private LineDao lineDao;
    private SectionService sectionService;

    @Autowired
    public LineController(LineDao lineDao, SectionService sectionService) {
        this.lineDao = lineDao;
        this.sectionService = sectionService;
    }

    @PostMapping("")
    public ResponseEntity<LineResponse> createLine(@RequestBody @Valid LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        long id = lineDao.save(line);
        Section sectionAddDto = new Section(id, lineRequest.getUpStationId(),
            lineRequest.getDownStationId(),
            lineRequest.getDistance());
        sectionService.save(sectionAddDto);
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
    public ResponseEntity<StationsInLineResponse> showLineDetail(@PathVariable Long id) {
        Line line = lineDao.findById(id);

        return ResponseEntity.ok()
            .body(new StationsInLineResponse(line, sectionService.findStationsByLineId(id)));
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> modifyLineDetail(@PathVariable Long id,
        @RequestBody LineRequest lineRequest) {
        lineDao.update(id, lineRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/{id}/sections", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> createSection(@PathVariable Long id,
        @RequestBody SectionRequest sectionRequest) {
        sectionService.insertSection(id, sectionRequest);

        return ResponseEntity.created(URI.create("/lines/" + id)).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLine(@PathVariable Long id) {
        lineDao.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/sections")
    public ResponseEntity deleteSection(@PathVariable Long id, @RequestParam Long stationId) {
        sectionService.deleteByUpStationId(id, stationId);
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

    @ExceptionHandler(DeleteMinimumSizeException.class)
    public ResponseEntity handleNoDelete() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(ShortDistanceException.class)
    public ResponseEntity handleShortDistance() {
        return ResponseEntity.badRequest().build();
    }
}
