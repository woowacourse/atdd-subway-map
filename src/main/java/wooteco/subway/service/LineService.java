package wooteco.subway.service;

import java.util.List;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.line.Line;

public class LineService {

    public Line createLine(String name, String color) {
        validateDuplication(name);
        Line line = new Line(name, color);
        return LineDao.save(line);
    }

    private void validateDuplication(String name) {
        boolean isDuplicated = LineDao.findAll()
            .stream()
            .anyMatch(line -> line.getName().equals(name));
        if (isDuplicated) {
            throw new IllegalArgumentException("중복!");
        }
    }

    public List<Line> findAll() {
        return LineDao.findAll();
    }

    public Line findById(long id) {
        return LineDao.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 노선이 존재하지 않습니다."));
    }

    public void editLine(long id, String name, String color) {
        validateDuplication(name);
        Line targetLine = findById(id);
        LineDao.updateLine(targetLine, name, color);
    }

    public void deleteLine(long id) {
        LineDao.deleteById(id);
    }
}
