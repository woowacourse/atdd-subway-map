package wooteco.subway.dao;

import wooteco.subway.domain.Line;

import java.util.List;
import java.util.Optional;

public interface LineRepository {

    Line save(Line line);

    List<Line> findAll();

    Optional<Line> findById(Long id);

    Optional<Line> findByName(String name);

    void update(Long id, Line line);

    void deleteById(Long id);

    boolean existByName(String name);
}
