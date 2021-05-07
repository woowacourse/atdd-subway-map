package wooteco.subway.line.repository;

import wooteco.subway.line.Line;

import java.util.List;
import java.util.Optional;

public interface LineRepository {
    Line save(Line line);

    List<Line> findAll();

    boolean validateDuplicateName(String name);

    Optional<Line> findById(Long id);

    void updateById(Long id, Line updatedLine);

    void delete(Long id);
}
