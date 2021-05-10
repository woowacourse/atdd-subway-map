package wooteco.subway.line;

import java.util.List;
import java.util.Optional;

public interface LineDao {
    Line save(Line line);

    List<Line> findAll();

    void update(Long id, String name, String color);

    Optional<Line> findById(Long id);

    void delete(Long id);

    Optional<Line> findByName(String name);

    Optional<Line> findByColor(String color);
}
