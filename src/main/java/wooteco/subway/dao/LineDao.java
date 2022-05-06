package wooteco.subway.dao;

import wooteco.subway.domain.Line;

import java.util.List;
import java.util.Optional;

public interface LineDao {

    Long save(Line line);

    Optional<Line> findById(Long id);

    List<Line> findAll();

    boolean hasLine(String name);

    void updateById(Long id, String name, String color);

    void deleteById(Long id);
}
