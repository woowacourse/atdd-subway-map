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
        if (lineNameOrColorExists(line.getName(), line.getColor())) {
            throw new IllegalArgumentException("노선 이름 또는 색이 이미 존재합니다.");
        }
        Line newLine = lineDao.save(line);
        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
    }

    private boolean lineNameOrColorExists(String name, String color) {
        return lineDao.countName(name) > 0 || lineDao.countColor(color) > 0;
    }

    public List<LineResponse> findAll() {
        List<Line> lines = lineDao.findAll();
        return lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        if (lineIdNotExists(id)) {
            throw new IllegalArgumentException("노선 ID가 존재하지 않습니다.");
        }
        Line line = lineDao.findById(id);
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }

    public void updateById(Long id, Line line) {
        if (lineIdNotExists(id)) {
            throw new IllegalArgumentException("노선 ID가 존재하지 않습니다.");
        }
        if (lineNameOrColorExistsWithDifferentId(line.getName(), line.getColor(), id)) {
            throw new IllegalArgumentException("노선 이름 또는 색이 이미 존재합니다.");
        }
        lineDao.updateById(id, line);
    }

    private boolean lineNameOrColorExistsWithDifferentId(String name, String color, Long id) {
        return lineDao.countNameWithDifferentId(name, id) > 0 || lineDao.countColorWithDifferentId(color, id) > 0;
    }

    public void deleteById(Long id) {
        if (lineIdNotExists(id)) {
            throw new IllegalArgumentException("노선 ID가 존재하지 않습니다.");
        }
        lineDao.deleteById(id);
    }

    private boolean lineIdNotExists(Long id) {
        return lineDao.countId(id) == 0;
    }
}
