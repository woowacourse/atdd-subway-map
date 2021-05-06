package wooteco.subway.dao.line;

import java.util.List;
import wooteco.subway.domain.line.Line;

public interface LineDao {

    Line save(Line line);

    List<Line> findAll();

    Line findById(Long id);

    void update(Line updatedLine);

    void deleteById(Long id);
}
