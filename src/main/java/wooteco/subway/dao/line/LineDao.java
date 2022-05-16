package wooteco.subway.dao.line;

import java.util.List;
import wooteco.subway.domain.Line;

public interface LineDao {

    long save(Line line);

    boolean existLineById(Long id);

    boolean existLineByName(String name);

    boolean existLineByColor(String color);

    List<Line> findAll();

    Line findById(Long id);

    void update(Line line);

    void delete(Long id);
}
