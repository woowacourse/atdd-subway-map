package wooteco.subway.line.repository;

import wooteco.subway.line.Line;

import java.util.List;
import java.util.Optional;

public interface LineRepository {
    Line save(Line line);

    List<Line> findAll();

    Optional<Line> findById(Long id);

    void update(Line currentLine, Line updatedLine);

    void delete(Line line);
}
