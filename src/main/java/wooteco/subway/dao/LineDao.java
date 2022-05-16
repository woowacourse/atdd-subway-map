package wooteco.subway.dao;

import wooteco.subway.domain.Line;

import java.util.List;

public interface LineDao {

    Line create(Line line);

    Line findById(Long id);

    List<Line> findAll();

    void update(Long id, String name, String color);

    void deleteById(Long id);

    boolean existById(Long id);

    boolean existByName(String name);

    boolean existByColor(String color);
}
