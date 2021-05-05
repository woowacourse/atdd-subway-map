package wooteco.subway.line;

import java.util.List;

public interface LineDao {
    Line save(Line line);

    List<Line> findAll();

    Line findById(Long id);

    boolean checkExistName(LineName name);

    boolean checkExistColor(LineColor color);

    boolean checkExistId(Long id);

    void update(Line line);

    void delete(Line line);

    void deleteAll();
}
