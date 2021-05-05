package wooteco.subway.line;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LineService {

    private final LineDao lineDao;

    private LineService(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    public Line add(String name, String color) {
        return lineDao.save(new Line(name, color));
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
