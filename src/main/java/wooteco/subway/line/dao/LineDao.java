package wooteco.subway.line.dao;

import java.util.List;
import wooteco.subway.line.Line;

public interface LineDao {

    Line save(Line line);

    List<Line> findAll();

    Line findById(Long id);

    void update(Long id, String name, String color);

    void deleteById(Long id);
}
