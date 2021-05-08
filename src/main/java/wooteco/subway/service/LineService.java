package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.domain.line.Line;
import wooteco.subway.exceptions.LineDuplicationException;
import wooteco.subway.exceptions.LineNotFoundException;
import wooteco.subway.repository.LineDao;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line createLine(String name, String color) {
        validateDuplication(name);
        Line line = new Line(name, color);
        long id = lineDao.save(line);
        line.setId(id);
        return line;
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        return lineDao.findById(id)
            .orElseThrow(LineNotFoundException::new);
    }

    public void editLine(Line line) {
        validateDuplication(line.getName());
        lineDao.updateLine(line);
    }

    public void deleteLine(Long id) {
        lineDao.deleteById(id);
    }

    private void validateDuplication(String name) {
        boolean isDuplicated = lineDao.findAll()
            .stream()
            .anyMatch(line -> line.getName().equals(name));
        if (isDuplicated) {
            throw new LineDuplicationException();
        }
    }
}
