package wooteco.subway.dao.line;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.line.Line;

public interface LineDao {

    Line save(Line line);

    List<Line> findAll();

    Optional<Line> findById(Long id);

    boolean isExistName(String name);

    void update(Line updatedLine);

    void deleteById(Long id);
}
