package wooteco.subway.line;

import java.util.List;

public interface LineDao {
    Line save(Line line);

    List<Line> findAll();

    void update(Long id, String name, String color);

    Line findById(Long id);

    void delete(Long id);
}
