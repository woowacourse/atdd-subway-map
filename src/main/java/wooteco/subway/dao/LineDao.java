package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Line;

public interface LineDao {

    Line save(Line line);

    Line findById(Long id);

    List<Line> findAll();

    boolean existByName(String name);

    boolean existById(Long id);

    void update(Line line);

    void delete(Long id);
}
