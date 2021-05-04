package wooteco.subway.line;

import java.util.List;

public class LineService {

    private static LineService instance;

    private LineService() {

    }

    public static LineService getInstance() {
        if (instance == null) {
            instance = new LineService();
        }
        return instance;
    }

    public Line add(String name, String color) {
        return LineDao.save(new Line(name, color));
    }

    public List<Line> lines() {
        return LineDao.findAll();
    }

    public Line line(Long id) {
        return LineDao.findById(id);
    }

    public void update(Long id, String name, String color) {
        LineDao.update(id, name, color);
    }

    public void delete(Long id) {
        LineDao.delete(id);
    }
}
