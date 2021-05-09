package wooteco.subway.dao.line;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.line.Line;

public interface LineDao {

    Line save(Line line);

    List<Line> findAll();

    Optional<Line> findById(Long id);

    boolean doesNotExistName(String name);

    boolean doesNotExistId(Long id);

    void update(Line updatedLine);

    void deleteById(Long id);
}
