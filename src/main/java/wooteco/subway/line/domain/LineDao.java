package wooteco.subway.line.domain;

import java.util.List;
import java.util.Optional;

public interface LineDao {
    Line save(Line line);

    List<Line> findAll();

    Optional<Line> findById(Long id);

    Optional<Line> findByName(String name);

    void clear();

    void update(Long id, String name, String color);

    void delete(Long id);
}
