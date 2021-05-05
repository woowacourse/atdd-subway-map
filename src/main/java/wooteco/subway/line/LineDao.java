package wooteco.subway.line;

import java.util.List;

public interface LineDao {
    Line save(Line line);
    Line findOne(Long id);
    List<Line> findAll();
    void update(int id, Line line);
    void delete(long id);
}
