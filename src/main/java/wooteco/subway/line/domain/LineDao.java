package wooteco.subway.line.domain;

import java.util.List;
import java.util.Optional;

public interface LineDao {
    LineEntity save(LineEntity lineEntity);

    List<LineEntity> findAll();

    Optional<LineEntity> findById(Long id);

    Optional<LineEntity> findByName(String name);

    void clear();

    void update(Long id, String name, String color);

    void delete(Long id);
}
