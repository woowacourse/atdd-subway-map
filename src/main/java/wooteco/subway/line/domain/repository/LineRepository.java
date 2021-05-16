package wooteco.subway.line.domain.repository;

import wooteco.subway.line.domain.Line;

import java.util.List;
import java.util.Optional;

public interface LineRepository {

    Line save(Line line);

    List<Line> findAll();

    Optional<Line> findById(Long id);

    Optional<Line> findByName(String name);

    void delete(Long id);

    void update(Line line);

    void deleteAll();
}
