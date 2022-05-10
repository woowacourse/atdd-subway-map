package wooteco.subway.dao;

import wooteco.subway.domain.Line;

import java.util.List;
import java.util.Optional;

public interface LineDao {
    long save(Line line);

    Optional<Line> findById(Long id);

    List<Line> findAll();

    boolean existById(Long id);

    boolean existByName(String name);

    boolean existByColor(String color);

    void update(Line line);

    void deleteById(Long id);

    void deleteAll();
}
