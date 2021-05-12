package wooteco.subway.dao;

import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

import java.util.List;
import java.util.Optional;

@Repository
public interface LineDao {
    Line save(Line line);

    Optional<Line> findById(Long id);

    Optional<Line> findByName(String lineName);

    List<Line> findAll();

    void delete(Long id);

    void update(Line newLine);
}
