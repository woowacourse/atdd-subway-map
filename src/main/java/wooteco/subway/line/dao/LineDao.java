package wooteco.subway.line.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import wooteco.subway.line.domain.Line;

@Repository
public interface LineDao {
    Line save(Line line);

    Optional<Line> findById(Long id);

    Optional<Line> findByName(String lineName);

    List<Line> findAll();

    void delete(Long id);

    void update(Line newLine);
}
