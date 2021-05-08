package wooteco.subway.line.domain;

import java.util.List;
import java.util.Optional;

public interface LineRepository {

    Line save(final Line line);

    Optional<Line> findByName(final String name);

    Optional<Line> findById(final Long id);

    List<Line> findAll();

    void update(final Line line);

    void delete(final Long id);
}
