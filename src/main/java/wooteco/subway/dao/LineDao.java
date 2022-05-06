package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.Line;

public interface LineDao {

    long save(Line line);

    boolean existLineById(Long id);

    boolean existLineByName(String name);

    boolean existLineByColor(String color);

    List<Line> findAll();

    Optional<Line> find(Long id);

    void update(long id, Line line);

    void delete(Long id);
}
