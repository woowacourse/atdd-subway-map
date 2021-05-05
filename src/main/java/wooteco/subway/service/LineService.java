package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.line.Line;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line createLine(String name, String color) {
        validateDuplication(name);
        Line line = new Line(name, color);
        return lineDao.save(line);
    }

    private void validateDuplication(String name) {
        boolean isDuplicated = lineDao.findAll()
            .stream()
            .anyMatch(line -> line.getName().equals(name));
        if (isDuplicated) {
            throw new IllegalArgumentException("중복!");
        }
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(long id) {
        return lineDao.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 노선이 존재하지 않습니다."));
    }

    public void editLine(long id, String name, String color) {
        validateDuplication(name);
        Line targetLine = findById(id);
        lineDao.updateLine(targetLine, name, color);
    }

    public void deleteLine(long id) {
        lineDao.deleteById(id);
    }
}
