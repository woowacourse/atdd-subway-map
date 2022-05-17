package wooteco.subway.dao;

import java.util.List;
import wooteco.subway.domain.Line;

public interface LineDao {

    Line save(Line line);

    Line findById(Long id);

    List<Line> findAll();

    boolean hasLine(String name);

    void updateById(Long id, String name, String color);

    void deleteById(Long id);
}
