package wooteco.subway.line.repository;

import wooteco.subway.line.Line;

import java.util.List;
import java.util.Optional;

public interface LineRepository {
    Line save(Line line);

    List<Line> findAll();

    Optional<Line> findById(Long id);

    void update(Long id, Line updatedLine);

    void delete(Long id);
}
