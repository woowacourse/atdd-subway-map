package wooteco.subway.line.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.line.Line;

public interface LineDao {

    Line save(Line line);

    Optional<Line> show(Long id);

    int countByName(String name);

    int countByColor(String color);

    List<Line> showAll();

    int update(long id, Line line);

    int delete(long id);
}
