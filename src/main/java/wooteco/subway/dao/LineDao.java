package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;

import wooteco.subway.domain.Line;

public interface LineDao {

    Line save(Line line);

    Optional<Line> findById(Long id);

    Optional<Line> findByName(String name);

    List<Line> findAll();

    void update(Line line);

    int deleteById(Long id);
}
