package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.repository.LineDao;
import wooteco.subway.domain.line.Line;

@Service
public class LineService {

    private final LineDao lineDao;

    public LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line createLine(String name, String color) {
        Line line = new Line(name, color);
        long id = lineDao.save(line);
        return findById(id);
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        return lineDao.findById(id);
    }

    public void editLine(Long id, String name, String color) {
        lineDao.updateLine(id, name, color);
    }

    public void deleteLine(Long id) {
        lineDao.deleteById(id);
    }
}
