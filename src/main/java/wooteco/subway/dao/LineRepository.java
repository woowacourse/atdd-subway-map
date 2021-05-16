package wooteco.subway.dao;

import wooteco.subway.domain.Line;

import java.util.List;
import java.util.Optional;

public interface LineRepository {
    Line save(Line line);

    List<Line> findAll();

    Line findById(Long id);

    Optional<Line> findByName(String name);

    Line update(Long id, Line newLine);

    void delete(Long id);

    boolean exists(long id);
}
