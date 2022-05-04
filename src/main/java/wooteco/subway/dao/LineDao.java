package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.Line;

public interface LineDao {
    
    Line save(String name, String color);

    Optional<Line> findById(Long id);

    void deleteAll();

    List<Line> findAll();

    void deleteById(Long id);

    void update(Line line);
}
