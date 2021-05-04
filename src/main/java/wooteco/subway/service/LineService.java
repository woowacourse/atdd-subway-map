package wooteco.subway.service;

import java.util.List;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.line.Line;

public class LineService {

    public Line createLine(String name, String color) {
        List<Line> lines = LineDao.findAll();
        boolean isDuplicated = lines.stream()
            .anyMatch(line -> line.getName().equals(name));
        if (isDuplicated) {
            throw new IllegalArgumentException("중복!");
        }
        Line line = new Line(name, color);
        return LineDao.save(line);
    }

}
