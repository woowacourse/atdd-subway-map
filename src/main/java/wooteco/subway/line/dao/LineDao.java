package wooteco.subway.line.dao;

import wooteco.subway.domain.Line;

import java.util.List;

public interface LineDao {
    Line create(Line line);

    boolean existByInfo(String name, String color);

    Line findById(Long lineId);

    boolean existById(Long lineId);

    List<Line> showAll();
}
