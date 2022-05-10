package wooteco.subway.repository.dao;

import java.util.List;
import java.util.Optional;

import wooteco.subway.domain.line.Line;

public interface LineDao {

    Long save(Line line);

    List<Line> findAll();

    Optional<Line> findById(Long id);

    Boolean existsByName(String name);

    Boolean existsByColor(String color);

    void update(Long id, String name, String color);

    void remove(Long id);
}
