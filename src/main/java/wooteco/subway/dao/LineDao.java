package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Line;

public interface LineDao {
    Long save(Line line);

    List<Line> findAll();

    Line findById(Long id);

    Long update(Long id, String name, String color);

    Long deleteById(Long id);
}
