package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.DuplicateLineException;
import wooteco.subway.exception.NoSuchLineException;

public interface LineDao {
    Line save(Line line) throws DuplicateLineException;

    List<Line> findAll();

    Optional<Line> findById(Long id);

    void update(Long id, String name, String color) throws NoSuchLineException, DuplicateLineException;

    void deleteById(Long id);
}
