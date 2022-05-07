package wooteco.subway.dao;

import java.util.List;

import wooteco.subway.domain.Line;

public interface LineDao {

    Long save(Line line);

    List<Line> findAll();

    Line findById(Long id);

    Boolean existsByName(String name);

    Boolean existsByColor(String color);

    void update(Long id, String name, String color);

    void remove(Long id);
}
