package wooteco.subway.infra.repository;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.Line;

public interface LineRepository {

    List<Line> findAll();

    Optional<Line> findById(Long id);

    Line save(Line line);

    boolean existByName(String name);

    boolean existByColor(String color);

    long update(Line line);

    long deleteById(Long id);

    boolean existSameNameWithDifferentId(String name, Long id);

    boolean existById(Long id);
}
