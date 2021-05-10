package wooteco.subway.dao.line;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.line.Line;

public interface LineDao {

    Line save(Line line);

    List<Line> findAll();

    Optional<Line> findById(Long id);

    boolean existsByName(String name);

    boolean existsById(Long id);

    void update(Line updatedLine);

    void deleteById(Long id);
}
