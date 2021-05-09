package wooteco.subway.line.dao;

import wooteco.subway.line.Line;

import java.util.List;
import java.util.Optional;

public interface LineDao {

    boolean existsByName(String name);

    Line save(Line line);

    List<Line> findAll();

    boolean existsById(Long id);

    Line findById(Long id);

    void removeById(Long id);

    void update(Long id, Line line);
}
