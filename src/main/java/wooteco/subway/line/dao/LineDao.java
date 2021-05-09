package wooteco.subway.line.dao;

import wooteco.subway.line.domain.Line;

import java.util.List;
import java.util.Optional;

public interface LineDao {

    Line save(Line line);

    List<Line> findAll();

    Optional<Line> findById(Long id);

    void delete(Long id);

    void update(Line line, Long id);

    void deleteAll();

    Optional<Line> findByName(String name);
}
