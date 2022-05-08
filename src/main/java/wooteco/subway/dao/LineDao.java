package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.Line;

public interface LineDao {

    Line save(Line line);

    Line saveWithId(Long id, Line line);

    boolean existsByName(String name);

    List<Line> findAll();

    Optional<Line> findById(Long id);

    void update(Long id, Line updateLine);

    void deleteById(Long id);
}
