package wooteco.subway.line;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LineDao {
    Line save(Line line);

    List<Line> findAll();

    void update(Long id, String name, String color);

    Optional<Line> findById(Long id);

    void delete(Long id);

    Optional<Line> findByName(String name);

    Optional<Line> findByColor(String color);
}
