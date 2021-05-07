package wooteco.subway.line;

import java.util.List;
import java.util.Optional;

public interface LineDao {

    Line save(LineRequest lineRequest);

    List<Line> findAll();

    Optional<Line> findById(Long id);

    Optional<Line> findByName(String name);

    void update(Long id, LineRequest lineRequest);

    void delete(Long id);
}
