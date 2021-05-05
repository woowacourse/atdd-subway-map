package wooteco.subway.line;

import java.util.List;

public interface LineDao {
    Line save(Line line);

    List<Line> findAll();

    Line findById(Long id);

    void update(Line line);

    void delete(Line line);

    void deleteAll();
}
