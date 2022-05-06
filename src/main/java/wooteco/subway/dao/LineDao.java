package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.Line;

public interface LineDao {
    Long save(Line line);

    List<Line> findAll();

    Optional<Line> findById(Long id);

    Long update(Long id, String name, String color);

    Long deleteById(Long id);
}
