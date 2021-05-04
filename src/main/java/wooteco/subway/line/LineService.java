package wooteco.subway.line;

import java.util.List;

public class LineService {
    private LineDao lineDao;

    public LineService() {
        this.lineDao = new LineDao();
    }

    public Line createLine(String name, String color) {
        if (isStationExist(name)) {
            throw new IllegalArgumentException("존재하는 노선 이름입니다.");
        }
        return lineDao.save(new Line(name, color));
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    private boolean isStationExist(String name) {
        return lineDao.findAll()
                      .stream()
                      .anyMatch(line -> name.equals(line.getName()));
    }

    public Line findById(Long id) {
        return lineDao.findById(id);
    }
}
