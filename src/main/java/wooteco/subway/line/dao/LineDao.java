package wooteco.subway.line.dao;

import wooteco.subway.line.Line;

import java.util.List;
import java.util.Optional;

public interface LineDao {

    Optional<Line> findByName(String name);

    Line save(Line line);

    List<Line> findAll();

    Optional<Line> findById(Long id);

    void removeById(Long id);

    void update(Long id, Line line);
}
