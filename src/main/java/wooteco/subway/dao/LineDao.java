package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Line;

public interface LineDao {

    Line insert(Line line);

    Line findById(Long id);

    Boolean existByName(Line line);

    Boolean existByColor(Line line);

    List<Line> findAll();

    void update(Long id, String name, String color);

    void delete(Long id);
}
