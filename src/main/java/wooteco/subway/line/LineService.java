package wooteco.subway.line;

import org.springframework.stereotype.Service;
import wooteco.subway.exception.LineDuplicationException;

import java.util.List;

@Service
public class LineService {

    private final LineDao lineDao;

    private LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line add(String name, String color) {
        List<Line> lines = lineDao.findAll();
        Line line = new Line(name, color);
        validateDuplicatedLine(lines, line);
        return lineDao.save(line);
    }

    private void validateDuplicatedLine(List<Line> lines, Line newLine) {
        if (isDuplicatedColor(lines, newLine)) {
            throw new LineDuplicationException();
        }
    }

    private boolean isDuplicatedColor(List<Line> lines, Line newLine) {
        return lines.stream()
                .anyMatch(line -> line.isSameColor(newLine));
    }

    public List<Line> lines() {
        return lineDao.findAll();
    }

    public Line line(Long id) {
        return lineDao.findById(id);
    }

    public void update(Long id, String name, String color) {
        lineDao.update(id, name, color);
    }

    public void delete(Long id) {
        lineDao.delete(id);
    }
}
