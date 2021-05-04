package wooteco.subway.line;

import java.util.List;

public class LineService {

    private final LineDao lineDao;
    private static LineService instance;

    private LineService() {
        this.lineDao = LineDao.getInstance();
    }

    public static LineService getInstance() {
        if (instance == null) {
            instance = new LineService();
        }
        return instance;
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
