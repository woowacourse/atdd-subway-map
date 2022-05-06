package wooteco.subway.ui;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@RequestMapping("/lines")
@RestController
public class LineController {

    private static final String LINE_DUPLICATION_NAME_EXCEPTION_MESSAGE = "중복되는 이름의 지하철 노선이 존재합니다.";
    private static final String LINE_DUPLICATION_COLOR_EXCEPTION_MESSAGE = "중복되는 색깔의 지하철 노선이 존재합니다.";
    private static final String NO_SUCH_LINE_EXCEPTION_MESSAGE = "해당하는 ID의 지하철 노선이 존재하지 않습니다.";

    private final LineDao lineDao;

    public LineController(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        if (isDuplicateName(lineRequest.getName())) {
            throw new IllegalArgumentException(LINE_DUPLICATION_NAME_EXCEPTION_MESSAGE);
        }
        if (isDuplicateColor(lineRequest.getColor())) {
            throw new IllegalArgumentException(LINE_DUPLICATION_COLOR_EXCEPTION_MESSAGE);
        }
        Line newLine = lineDao.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        LineResponse lineResponse = new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
        return ResponseEntity.created(URI.create("/lines/" + newLine.getId())).body(lineResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LineResponse>> showLines() {
        List<Line> lines = lineDao.findAll();
        List<LineResponse> lineResponses = lines.stream()
            .map(it -> new LineResponse(it.getId(), it.getName(), it.getColor()))
            .collect(Collectors.toList());
        return ResponseEntity.ok().body(lineResponses);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LineResponse> showLine(@PathVariable Long id) {
        Line line = findLineById(id);
        LineResponse lineResponse = new LineResponse(line.getId(), line.getName(), line.getColor());
        return ResponseEntity.ok().body(lineResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        Line line = findLineById(id);
        lineDao.delete(line);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        Line originLine = findLineById(id);
        if (isDuplicateName(lineRequest.getName()) && isNotSameName(originLine.getName(), lineRequest.getName())) {
            throw new IllegalArgumentException(LINE_DUPLICATION_NAME_EXCEPTION_MESSAGE);
        }
        if (isDuplicateColor(lineRequest.getColor()) && isNotSameColor(originLine.getColor(), lineRequest.getColor())) {
            throw new IllegalArgumentException(LINE_DUPLICATION_COLOR_EXCEPTION_MESSAGE);
        }
        lineDao.update(originLine, new Line(id, lineRequest.getName(), lineRequest.getColor()));

        return ResponseEntity.ok().build();
    }

    private Line findLineById(Long id) {
        return lineDao.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(NO_SUCH_LINE_EXCEPTION_MESSAGE));
    }

    private boolean isDuplicateName(String name) {
        return lineDao.findByName(name).isPresent();
    }

    private boolean isDuplicateColor(String color) {
        return lineDao.findByColor(color).isPresent();
    }

    private boolean isNotSameName(String originName, String updateName) {
        return !originName.equals(updateName);
    }

    private boolean isNotSameColor(String originColor, String updateColor) {
        return !originColor.equals(updateColor);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<Map<String, String>> handle(RuntimeException exception) {
        return ResponseEntity.badRequest().body(Map.of(
            "message", exception.getMessage()
        ));
    }
}
