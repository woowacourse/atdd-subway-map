package wooteco.subway.line;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public LineResponse save(Line line) {
        if (lineDao.existsByNameOrColor(line.getName(), line.getColor())) {
            throw new IllegalArgumentException("노선 이름 또는 색이 이미 존재합니다.");
        }
        Line newLine = lineDao.save(line);
        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineDao.findById(id).orElseThrow(() -> new IllegalArgumentException("노선 ID가 존재하지 않습니다."));
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }

    public void updateById(Long id, Line line) {
        if (!lineDao.findById(id).isPresent()) {
            throw new IllegalArgumentException("노선 ID가 존재하지 않습니다.");
        }
        if (lineDao.existsByNameOrColorWithDifferentId(line.getName(), line.getColor(), id)) {
            throw new IllegalArgumentException("노선 이름 또는 색이 이미 존재합니다.");
        }
        lineDao.updateById(id, line);
    }

    public void deleteById(Long id) {
        if (!lineDao.findById(id).isPresent()) {
            throw new IllegalArgumentException("노선 ID가 존재하지 않습니다.");
        }
        lineDao.deleteById(id);
    }
}
