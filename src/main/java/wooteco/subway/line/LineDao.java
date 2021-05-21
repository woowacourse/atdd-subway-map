package wooteco.subway.line;

import java.util.List;
import java.util.Optional;

public interface LineDao {
    Line save(Line line);

    List<Line> findAll();

    Optional<Line> findById(Long id);

    Optional<Line> findByName(String name);

    Optional<Line> findByColor(String color);

    void update(Long id, String name, String color);

    void delete(Long id);
}
