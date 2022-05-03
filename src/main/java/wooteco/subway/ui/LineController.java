package wooteco.subway.ui;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@RestController
public class LineController {

    private final LineDao lineDao;

    public LineController(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Optional<Line> wrappedStation = lineDao.findByName(lineRequest.getName());
        if (wrappedStation.isPresent()) {
            throw new IllegalArgumentException("이미 같은 이름의 노선이 존재합니다.");
        }
        Line savedLine = lineDao.save(line);
        return ResponseEntity.created(URI.create("/lines/" + savedLine.getId())).body(LineResponse.of(line));
    }

    @GetMapping("/lines")
    public List<LineResponse> getAllLines() {
        List<Line> allLines = lineDao.findAllLines();
        return allLines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @GetMapping("/lines/{lineId}")
    public LineResponse getLineById(@PathVariable Long lineId) {
        Optional<Line> wrappedLine = lineDao.findById(lineId);
        if (wrappedLine.isEmpty()) {
            throw new IllegalArgumentException("해당 노선이 존재하지 않습니다.");
        }
        return LineResponse.of(wrappedLine.get());
    }

    @PutMapping("/lines/{lineId}")
    public void updateLine(@PathVariable Long lineId, @RequestBody LineRequest lineRequest) {
        Line newLine = new Line(lineRequest.getName(), lineRequest.getColor());
        lineDao.updateLine(lineId, newLine);
    }

    @DeleteMapping("/lines/{lineId}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long lineId) {
        lineDao.deleteById(lineId);
        return ResponseEntity.noContent().build();
    }
}
