package wooteco.subway.line.domain;

import java.util.List;
import java.util.Optional;

public interface LineDao {
    Line save(Line line);

    List<Line> findAll();

    Optional<Line> findById(Long id);

    Optional<Line> findByName(String name);

    void clear();

    void update(Line line);

    void delete(Long id);

    boolean existByName(String name);

    boolean existByColor(String color);
}
