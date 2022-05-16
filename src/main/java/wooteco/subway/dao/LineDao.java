package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.NoSuchLineException;

public interface LineDao {
    Line save(Line line) throws IllegalArgumentException;

    List<Line> findAll();

    Optional<Line> findById(Long id);

    void update(Line line) throws NoSuchLineException, IllegalArgumentException;

    void deleteById(Long id);

    boolean existByName(String name);

    boolean existByColor(String color);
}
