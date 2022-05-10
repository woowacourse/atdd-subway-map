package wooteco.subway.repository.dao;

import java.util.List;
import wooteco.subway.repository.entity.LineEntity;

public interface LineDao {

    LineEntity save(final LineEntity lineEntity);

    List<LineEntity> findAll();

    LineEntity findById(final Long id);

    void deleteById(final Long id);

    void update(final LineEntity newLineEntity);
}
