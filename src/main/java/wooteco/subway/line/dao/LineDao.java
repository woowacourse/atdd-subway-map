package wooteco.subway.line.dao;

import wooteco.subway.domain.Line;

import java.util.List;

public interface LineDao {
    Line create(Line line);

    boolean existByNameAndColor(String name, String color);

    List<Line> showAll();

    Line findById(Long lineId);

    boolean existById(Long lineId);
}
