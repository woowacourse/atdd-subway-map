package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.DuplicateLineException;

public interface LineDao {
    Line save(Line line) throws DuplicateLineException;

    List<Line> findAll();

    Optional<Line> findById(Long id);

    Long update(Long id, String name, String color);

    Long deleteById(Long id);
}
