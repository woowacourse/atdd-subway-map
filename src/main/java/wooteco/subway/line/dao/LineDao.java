package wooteco.subway.line.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.line.Line;

public interface LineDao {

    Optional<Line> findLineByName(String name);

    Line save(Line line);

    List<Line> findAll();

    Optional<Line> findLineById(Long id);

    void removeLine(Long id);

    void update(Long id, String name, String color);

    Optional<Line> findLineByNameOrColor(String name, String color);
}
