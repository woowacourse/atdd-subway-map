package wooteco.subway.dao;

import java.util.List;

import wooteco.subway.domain.Line;

public interface LineDao {
    Line save(Line line);

    boolean existByName(String name);

    List<Line> findAll();

    Line find(Long id);

    boolean existById(Long id);

    void update(Line line);

    void delete(Long id);
}
