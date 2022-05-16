package wooteco.subway.dao;

import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.Line;

public interface LineDao {

    Long save(Line line);

    List<Line> findAll();

    boolean deleteById(Long id);

    Optional<Line> findById(Long id);

    boolean updateById(Long id, Line line);

    boolean existsByName(String name);
}
