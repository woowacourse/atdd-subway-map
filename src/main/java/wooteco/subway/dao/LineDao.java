package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;

import wooteco.subway.domain.Line;

public interface LineDao {
    Optional<Line> save(Line line);

    List<Line> findAll();

    Optional<Line> findById(Long id);

    boolean update(Line line);

    boolean delete(Long id);
}
